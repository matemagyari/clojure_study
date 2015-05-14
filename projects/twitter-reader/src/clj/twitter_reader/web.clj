(ns twitter-reader.web
  (:require [twitter-reader.tweet-processor :as tp]
            [twitter-reader.twitter-listener :as tl]
            [org.httpkit.server :as kit]
            [clojure.string :as str]
            [clojure.data.json :as json]))

;; Storing connections and statistics as a map of ws-channel -> stats
(def conns (atom {}))

(defn reset-conn! [conn]
  (swap! conns assoc conn {:stats {:word-occurrences {}
                                   :tweet-count 0}}))
(defn remove-conn! [conn]
  (swap! conns dissoc conn))

(defn update-stats! [conn stats]
  (swap! conns assoc-in [conn :stats] stats))
;; =============================================

(defn- ui-message
  "Creates a UI message from the latest tweet and the fresh statistics"
  [stats tweet]
  (json/write-str
    {:stats (into (sorted-map) (:word-occurrences stats))
     :tweet {:text (:text tweet)
             :user (get-in tweet [:user :name])}}))

(defn handle-tweet!
  "Updates the model when new tweet arrives for the given connection"
  [tweet ws-channel words]
  ;; only when connection isn't closed
  (when-let [stats (get-in @conns [ws-channel :stats])]
    (let [updated-stats (tp/process-tweet {:tweet-text (:text tweet)
                                           :stats stats
                                           :search-words words})]
      (update-stats! ws-channel updated-stats)
      (kit/send! ws-channel (ui-message updated-stats tweet)))))

(defn ws-handler
  "Websocket endpoint"
  [twitter-stream-factory tweet-listener request]
  (kit/with-channel request channel
    (println channel "connected")
    (let [;twitter-stream (tl/create-twitter-stream twitter-stream-factory)
          on-close-f (fn [status]
                       (println "channel closed: " status)
                       (remove-conn! channel))
          on-receive-f (fn [words]
                         (println "received: " channel words)
                         (reset-conn! channel)
                         (tl/start-listener! tweet-listener words (fn [tweet]
                                                                    (handle-tweet! tweet channel (set (str/split words #","))))))]
      (kit/on-close channel on-close-f)
      (kit/on-receive channel on-receive-f))))

(defn -main [& args]
  (let [twitter-stream-factory (tl/create-twitter-stream-factory)
        tweet-listener (tl/create-tweet-listener)
        handler (fn [req] (ws-handler twitter-stream-factory tweet-listener req))]
    (kit/run-server handler {:port 8080})
    (println "Started")))




