(ns twitter-reader.twitter-listener
  (:require [clojure.edn :as edn]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [http.async.client :as ac]
            [twitter.callbacks :as tc]
            [twitter.callbacks.handlers :as tch]
            [twitter.api.streaming :as tas]
            [twitter.oauth :as oauth])
  (:import [twitter.callbacks.protocols AsyncStreamingCallback]
           [java.io ByteArrayInputStream]))

;; Tweet Listener Component.


(defn- create-oauth-creds
  "Creates OAuth credentials for the given config."
  [config]
  (oauth/make-oauth-creds
    (:consumer-key config)
    (:consumer-secret config)
    (:user-access-token config)
    (:user-access-token-secret config)))

(defn- read-oauth-creds
  "Reads up the OAuth credentials from a file and returns a map containing them"
  [file]
  (-> file slurp edn/read-string create-oauth-creds))

(defn- get-tweet-text
  "Extracts the text field of the tweet from the ByteArrayInputStream"
  [^ByteArrayInputStream baos]
  (when-let [tweet (try
                     (json/read-str (str baos))
                     (catch Exception e
                       (println "Bad tweet")
                       nil))]
    (get tweet "text")))

(defn- start-listening!
  "Starts listening"
  [{:keys [query oauth-creds callback]}]
  (tas/statuses-filter
    :params {:track query}
    :oauth-creds oauth-creds
    :callbacks (AsyncStreamingCallback.
                 (fn [response baos] ;; Tweet handler
                   (when-let [tweet-text (get-tweet-text baos)]
                     (callback tweet-text)))
                 tch/get-twitter-error-message ;; Tweet API error handler
                 tch/exception-print))) ;; exception handler))

;; ========== PUBLIC ===========================

(defprotocol TweetListener
  "Interface for grouping the functions of starting and stopping the tweet stream"
  (start-listener! [this] "Starts listening")
  (stop-listener! [this] "Stops listening"))

(defn create-tweet-listener
  "Factory function. Creates an instanse of the TweetListener protocol"
  [{:keys [query tweet-callback]}]
  (let [cancel-stream!-fn (atom nil)]
    (reify TweetListener
      (start-listener! [this]
        (let [creds (read-oauth-creds "/Users/mate.magyari/twitter.conf")
              user-stream (tas/user-stream :oauth-creds creds)]
          (start-listening! {:query query :oauth-creds creds :callback tweet-callback})
          (reset! cancel-stream!-fn (:cancel (meta user-stream)))))
      (stop-listener! [this]
        (@cancel-stream!-fn)))))




