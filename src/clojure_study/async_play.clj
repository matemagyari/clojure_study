(ns clojure-study.async-play)

(require '[clojure.core.async :as async :refer :all])

(let [c (chan)]
  (close! c))

(let [c (chan 10)]
  (>!! c "hello")
  (assert (= "hello" (<!! c)))
  (close! c))


