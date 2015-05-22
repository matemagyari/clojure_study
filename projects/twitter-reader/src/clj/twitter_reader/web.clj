(ns twitter-reader.web
  "Entry point of the application. Exposes a websocket endpoint."
  (:require [twitter-reader.tweet-processor :as tp]
            [twitter-reader.twitter-listener :as tl]
            [twitter-reader.conn-repository :as cr]
            [org.httpkit.server :as kit]
            [clojure.string :as str]
            [clojure.data.json :as json]))

(defn- ui-message
  "Creates a UI message from the latest tweet and the updated statistics"
  [stats tweet]
  (json/write-str
    {:stats (into (sorted-map) stats)
     :tweet tweet}))

(defn- handle-tweet!
  "Updates the model when new tweet arrives for the given connection and sends update to the UI"
  [tweet ws-channel]
  ;; only when connection isn't closed
  (when-let [conn (cr/get-conn ws-channel)]
    (let [update (tp/process-tweet {:text (:text tweet)
                                    :tweets (:tweets conn)
                                    :search-words (:search-words conn)
                                    :now (System/currentTimeMillis)})]
      (cr/update-tweets! ws-channel (:tweets update))
      (kit/send! ws-channel (ui-message (:word-frequencies update) tweet)))))

(defn- ws-handler
  "Websocket request handler"
  [ts-factory request]
  (kit/with-channel request channel
    (println channel "connected")
    (let [tweet-listener (tl/create-tweet-listener ts-factory (fn [tweet]
                                                                (handle-tweet! tweet channel)))
          on-close-f (fn [status]
                       (println "channel closed: " status)
                       (cr/remove-conn! channel))
          on-receive-f (fn [data]
                         (println "received: " channel data)
                         (let [words (set (str/split data #","))]
                           (cr/reset-conn! channel words)
                           (tl/start-listener! tweet-listener words)))]
      (kit/on-close channel on-close-f)
      (kit/on-receive channel on-receive-f))))

(defn -main [& args]
  (let [twitter-stream-factory (tl/create-twitter-stream-factory)
        handler (fn [req] (ws-handler twitter-stream-factory req))]
    (kit/run-server handler {:port 8080})
    (println "Started")))




