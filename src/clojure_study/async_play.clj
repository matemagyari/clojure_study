(ns clojure-study.async-play
  (:use [clojure.core.async :only [chan go <! <!! >! >!! close!]]))


(println "111")

(let [c (chan)]
  (println "222")
  (close! c))

(let [c (chan 10)]
  (>!! c "hello")
  (assert (= "hello" (<!! c)))
  (close! c))

(def c1 (chan 10))
(go (while true
      (println (str "Incoming:" (<! c1)))))





