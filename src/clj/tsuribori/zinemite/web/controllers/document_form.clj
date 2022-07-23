(ns tsuribori.zinemite.web.controllers.document-form
  #_{:clj-kondo/ignore [:use]}
  (:use [amazonica.aws.s3])
  (:require [clojure.tools.logging :as log]
            [malli.core :as m]
            [malli.error :as me]
            [pantomime.mime :as pm]
            [ring.util.http-response :as http-response]
            [tsuribori.zinemite.web.pages.layout :as layout]
            [tsuribori.zinemite.web.routes.utils :as utils]))

(defn create-file-map
  [{:keys [tempfile size]}]
  (let [mime-type (pm/mime-type-of tempfile)]
    {:size-in-mb (/ size 1000000)
     :mime-type mime-type
     :tempfile tempfile
     :extension (pm/extension-for-name mime-type)}
    ))

(defn format-s3-url
  [{:keys [bucket], {:keys [endpoint]} :creds} filename]
  (str endpoint "/" bucket "/" filename))

(defn save-file
  [{:keys [tempfile extension]} uuid {:keys [bucket creds] :as s3}]
  (let [filename (str uuid extension)]
    (try
      #_{:clj-kondo/ignore [:unresolved-symbol]}
      (put-object
       creds
       :bucket-name bucket
       :key filename
       :file tempfile
       :access-control-list {:grant-permission ["AllUsers" "Read"]})
      (.delete tempfile)
      (format-s3-url s3 filename) ;; return S3 path
      (catch Exception e
        (log/error "Error saving file" uuid e)
        (throw Exception)))))

(defn document-form [{:keys [flash] :as request}]
  (log/debug "Form errors:" flash)
  (layout/render request "document_form.html"
                 {:errors (:errors flash)}))

(defn save-document!
  [{{:strs [title file]} :multipart-params :as request}]
  (let [{:keys [query-fn
                s3
                title-schema
                file-schema]} (utils/route-data request)
        file-map (create-file-map file)
        valid-title? (m/validate title-schema title)
        valid-file? (m/validate file-schema file-map)]
    (try
      (if (and valid-title? valid-file?)
        (let [uuid (str (java.util.UUID/randomUUID))]
          (log/info "Saving document" uuid)
          (query-fn :insert-document!
                    {:title title
                     :uuid uuid
                     :mime (:mime-type file-map)
                     :path (save-file file-map uuid s3)})
          (http-response/found (str "/z/" uuid)))
        (cond-> (http-response/found "/upload")
          (not valid-title?)
          (assoc-in
           [:flash :errors :title]
           (me/humanize (m/explain title-schema title)))
          (not valid-file?)
          (assoc-in
           [:flash :errors :file] 
           (me/humanize (m/explain file-schema file-map)))))
      (catch Exception e
        (log/error e "Error saving document")
        (-> (http-response/found "/upload")
            (assoc-in [:flash :errors :unknown]
                      "Failed to save document. Please contact the administrator."))))))

(defn get-document
  [{{:keys [uuid]} :path-params :as request}]
  (log/info "Serving document" uuid)
  (let [{:keys [query-fn]} (utils/route-data request)]
    (layout/render request "document.html"
                   {:document (query-fn :select-document-by-uuid {:uuid uuid})})))