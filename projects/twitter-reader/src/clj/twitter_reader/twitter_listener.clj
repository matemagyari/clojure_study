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
           [java.io ByteArrayInputStream]
           [twitter4j TwitterStreamFactory TwitterStream StatusListener FilterQuery]
           [twitter4j.conf ConfigurationBuilder]))

;; Tweet Listener Component.


(defn- create-oauth-creds
  "Creates OAuth credentials for the given config."
  [config]
  (oauth/make-oauth-creds
    (:consumer-key config)
    (:consumer-secret config)
    (:user-access-token config)
    (:user-access-token-secret config)))

(defn- read-oauth-creds-2
  "Reads up the OAuth credentials from a file and returns a map containing them"
  [file]
  (-> file slurp edn/read-string))

(defn- read-oauth-creds
  "Reads up the OAuth credentials from a file and returns a map containing them"
  [file]
  (create-oauth-creds (read-oauth-creds-2 file)))

(defn- get-tweet
  "Extracts the text field of the tweet from the ByteArrayInputStream"
  [^ByteArrayInputStream baos]
  (when-let [tweet (try
                     (json/read-str (str baos))
                     (catch Exception e
                       (println "Bad tweet")
                       nil))]
    (if (and (map? tweet) (some? (get tweet "text")))
      (clojure.walk/keywordize-keys tweet))))

(defn- start-listening!
  "Starts listening"
  [{:keys [query oauth-creds callback]}]
  (tas/statuses-filter
    :params {:track query}
    :oauth-creds oauth-creds
    :callbacks (AsyncStreamingCallback.
                 (fn [response baos] ;; Tweet handler
                   (when-let [tweet (get-tweet baos)]
                     (callback tweet)))
                 tch/get-twitter-error-message ;; Tweet API error handler
                 tch/exception-print))) ;; exception handler))

;; ========== PUBLIC ===========================

(defprotocol TweetListener
  "Interface for grouping the functions of starting and stopping the tweet stream"
  (start-listener! [this query callback] "Starts listening")
  (stop-listener! [this] "Stops listening"))

(defn create-tweet-listener
  "Factory function. Creates an instance of the TweetListener protocol"
  []
  (let [creds (read-oauth-creds "/Users/mate.magyari/twitter.conf")
        user-stream (tas/user-stream :oauth-creds creds)
        cancel-stream!-fn (:cancel (meta user-stream))]
    (reify TweetListener
      (start-listener! [this query callback]
        (start-listening! {:query query
                           :oauth-creds creds
                           :callback callback}))
      (stop-listener! [this]
        (cancel-stream!-fn)))))


(defn- create-status-listener
  "Create an instance of StatusListener Java interface"
  [callback]
  (reify StatusListener
    (onException [this ex] (println ex))
    (onDeletionNotice [this notice] (println "Deletion Notice!" notice))
    (onScrubGeo [this a1 a2] (println "onScrubGeo!" a1 a2))
    (onTrackLimitationNotice [this a1] (println "onTrackLimitationNotice!" a1))
    (onStatus [this status] (let [tweet {:user (-> status .getUser .getScreenName)
                                         :text (.getText status)}]
                              (callback tweet)))))

(defn create-twitter-stream-factory
  "Create an instance of TwitterStreamFactory"
  []
  (let [creds (read-oauth-creds-2 "/Users/mate.magyari/twitter.conf")
        config-builder (doto (new ConfigurationBuilder)
                         (.setOAuthConsumerKey (:consumer-key creds))
                         (.setOAuthConsumerSecret (:consumer-secret creds))
                         (.setOAuthAccessToken (:user-access-token creds))
                         (.setOAuthAccessTokenSecret (:user-access-token-secret creds)))]
    (new TwitterStreamFactory
      (.build config-builder))))

(defn create-twitter-stream
  "Create an instance of TwitterStream"
  [ts-factory callback]
  (let [t-stream (.getInstance ts-factory)
        listener (create-status-listener callback)]
    (.addListener t-stream listener)))

(comment
(defprotocol TweetListener2
  "Interface for grouping the functions of starting and stopping the tweet stream"
  (start-listener! [this words] "Starts listening")
  (stop-listener! [this] "Stops listening"))

(defn create-tweet-listener2
  "Factory function. Creates an instance of the TweetListener protocol"
  [ts-factory callback]
  (let [t-stream (create-twitter-stream ts-factory callback)]
    (reify TweetListener2
      (start-listener! [this words]
        (let [listener (create-status-listener callback)
              keywords (into-array words) ; to java String []`
              query (doto (new FilterQuery)
                      (.track keywords))]
          (.filter t-stream query)))
      (stop-listener! [this] (.clearListeners t-stream)))))
  )
