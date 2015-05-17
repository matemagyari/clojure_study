(ns twitter-reader.twitter-listener
  "Wraps around Twitter4J Java library demonstrating the seamless Clojure-Java interoperability.
  Exposes a TwitterListener interface and 2 factory methods, one for the app's soleTwitterStreamFactory
  and one for TwitterListener"
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

(defn- create-twitter-stream
  "Create an instance of TwitterStream"
  [ts-factory callback]
  (let [t-stream (.getInstance ts-factory)
        listener (create-status-listener callback)]
    (.addListener t-stream listener)
    t-stream))

;; =============== PUBLIC ============================

(defprotocol TweetListener
  "Interface for grouping the functions of starting and stopping the tweet stream"
  (start-listener! [this words] "Starts listening")
  (stop-listener! [this] "Stops listening"))

(defn create-tweet-listener
  "Factory function. Creates an instance of the TweetListener protocol"
  [ts-factory callback]
  (let [t-stream (create-twitter-stream ts-factory callback)]
    (reify TweetListener
      (start-listener! [this words]
        (let [listener (create-status-listener callback)
              keywords (into-array words) ; to java String []`
              query (doto (new FilterQuery)
                      (.track keywords))]
          (.filter t-stream query)))
      (stop-listener! [this] (.clearListeners t-stream)))))

(defn create-twitter-stream-factory
  "Create an instance of TwitterStreamFactory"
  []
  (let [creds (-> "/Users/mate.magyari/twitter.conf" slurp edn/read-string)
        config-builder (doto (new ConfigurationBuilder)
                         (.setOAuthConsumerKey (:consumer-key creds))
                         (.setOAuthConsumerSecret (:consumer-secret creds))
                         (.setOAuthAccessToken (:user-access-token creds))
                         (.setOAuthAccessTokenSecret (:user-access-token-secret creds)))]
    (new TwitterStreamFactory
      (.build config-builder))))

