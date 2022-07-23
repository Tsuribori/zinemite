(ns tsuribori.zinemite.web.storage
  (:use [amazonica.aws.s3])
  (:require
   [integrant.core :as ig]))

(defmethod ig/init-key :storage/s3
  [_ {:keys [bucket creds]}]
  {:bucket bucket
   :creds creds})

(defmethod ig/halt-key! :storage/s3
 [_ _]
  nil)