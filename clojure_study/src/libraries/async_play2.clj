(ns libraries.async-play2
  (:require [clojure.core.async :refer [go go-loop chan close! <! <!! >! >!!
                                        timeout filter< put! take! pub sub unsub unsub-all
                                        thread alts! alts!!]
             :as a]
            [clojure-study.assertion :as ae]
            [clojure.test :as test]))



(def c (chan 100))
(def go-chan
  (a/go-loop []
    (println "Loops starts")
    (if-let [in-msg (a/<! c)]
      (do
        (println (str "Incoming: " in-msg))
        (recur))
      (println "Channel closed"))))

(defn -main []
  (do
    (println "start")
    (>!! c "First")
    (>!! c "Second")
    ))

(-main)