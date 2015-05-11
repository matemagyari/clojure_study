(ns patterns.producer-consumer
  (:require [clojure.core.async :as a]))

(defn consume! [msg container-atom]
  (let [current (get @container-atom msg 0)]
    (swap! container-atom assoc msg (inc current))))

(defn produce!
  "Puts messages in the channel"
  [msg a-channel]
  (a/go-loop []
    (a/>! a-channel msg)
    (recur)))

(defn start-consumer!
  "Consumes messages from the channel and puts them in a container grouped by content"
  [a-channel container-atom]
  (a/go-loop []
    (when-let [msg (a/<! a-channel)]
      (consume! msg container-atom)
      (recur))))

(defn start-producers!
  "Start a given number of producers on the given channel"
  [num-of-producers a-channel]
  (let [producers (atom [])]
    (doseq [x (range num-of-producers)
            :let [msg (str "a" x)]]
      (let [producer (produce! msg a-channel)]
        (swap! producers conj producer)))
    producers))


(defn analyse [result]
  (let [num-of-actives (-> result keys count)
        sum (reduce + (vals result))
        min-val (->> result vals (apply min))
        max-val (->> result vals (apply max))]
    (println (str
               "Number of active producers:" num-of-actives
               "\nNumber of messages:" sum
               "\nLaziest producer:" min-val
               "\nBusiest producer:" max-val))))

(def container (atom {}))
(def QUEUE (a/chan))
(def PRODUCER-NUM 100)

(start-consumer! QUEUE container)
(def producers (start-producers! PRODUCER-NUM QUEUE))

;;wait a second
(a/<!! (a/timeout 1000))

(doseq [producer @producers]
  (a/close! producer))

(analyse @container)




