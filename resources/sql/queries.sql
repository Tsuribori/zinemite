-- Place your queries here. Docs available https://www.hugsql.org/

-- :name insert-document! :! :n
-- :doc Insert a single document into the database.
INSERT INTO document (uuid, title, path, mime) VALUES (:uuid, :title, :path, :mime);

-- :name select-document-by-uuid :? :1
-- :doc Select a document by its UUID.
SELECT * FROM document WHERE uuid = :uuid;
