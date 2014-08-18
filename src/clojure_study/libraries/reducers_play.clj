(ns
  ^{:author mate.magyari}
  clojure-study.libraries.reducers-play
  (:require [clojure.core.reducers :as r]
            [clojure-study.assertion :as ae]
            [clojure.test :as t]))


(let [long-range (into [] (range 99))
      time-with (fn [f] (time
                          (f + long-range)))
      print-with (fn [f name]
                   (println (str name ": " (time-with f))))]
  (print-with reduce "reduce")
  (print-with r/reduce "r/reduce")
  (print-with r/fold "f/fold")
  (print-with reduce "reduce")
  (print-with r/reduce "r/reduce")
  (print-with r/fold "f/fold")
  )

(t/deftest xx
  (t/is false))