(ns twitter-reader.main
  (:require [twitter-reader.tweet-processor :as tp]
            [twitter-reader.twitter-listener :as tl]
            [clojure.core.async :as async]
            [clojure.string :as str]))


;; =============== DISPLAY ==============================
(defn start-displayer! [display-chan]
  (async/go-loop []
    (when-let [m (async/<! display-chan)]
      (println (str "Update: " m))
      (recur))))


;; =============== MAIN  ==============================
(defn -main []
  (let [display-chan (async/chan 100)
        search-expression "poker,virgin,gamesys"
        tweet-processor (tp/create-tweet-processor {:search-words (set (str/split search-expression #","))
                                                     :out-channel display-chan})
        tweet-listener (tl/create-tweet-listener
                         {:query search-expression
                          :tweet-callback (fn [tweet]
                                            (tp/handle-tweet! tweet-processor tweet))})]
    (start-displayer! display-chan)
    (tl/start-listener! tweet-listener)
    (Thread/sleep 10000)
    (tl/stop-listener! tweet-listener)))




