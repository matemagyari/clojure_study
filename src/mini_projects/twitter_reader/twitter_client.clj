(ns mini-projects.twitter-reader.twitter-client
  (:require [clojure.edn :as edn]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [http.async.client :as ac]
            [twitter.callbacks :as tc]
            [twitter.callbacks.handlers :as tch]
            [twitter.api.streaming :as tas]
            [twitter.oauth :as oauth])
  (:import (twitter.callbacks.protocols AsyncStreamingCallback)))


(defn- create-oauth-creds
  "Create OAuth credentials for the given config."
  [config]
  (oauth/make-oauth-creds
    (:consumer-key config)
    (:consumer-secret config)
    (:user-access-token config)
    (:user-access-token-secret config)))

(defn- read-oauth-creds [file]
  (-> file slurp edn/read-string create-oauth-creds))

;; our custom handler, it's called for each Tweet that comes in from the Streaming API.
(defn- handle-tweet [response baos]
  (when-let [tweet (try
                     (json/read-str (str baos))
                     (catch Exception e
                       (println (.getMessage e))))]
    (println (str "Tweet: " (get tweet "text")))))

; supply a callback that only prints the text of the status
(defn- create-streaming-callback []
  (AsyncStreamingCallback.
    handle-tweet ;; Tweet handler
    tch/get-twitter-error-message ;; Tweet API error handler
    tch/exception-print)) ;; exception handler

(defn start-listening! [query oauth-creds]
  (tas/statuses-filter
    :params {:track query}
    :oauth-creds oauth-creds
    ;:callbacks (tc/callbacks-async-streaming-default)
    :callbacks (create-streaming-callback)
    ))

(defn -main []
  (let [creds (read-oauth-creds "/Users/mate.magyari/twitter.conf")
        u-stream (tas/user-stream :oauth-creds creds)
        cancel-stream-fn (:cancel (meta u-stream))]
    (start-listening! "poker" creds)
    (Thread/sleep 10000)
    (cancel-stream-fn)))

(-main)

