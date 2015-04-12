(ns
  ^{:author mate.magyari}
  clojure-study.clojure.forcomps)

;multiple whens and lets possible
(println (for [x (range 5)
               :when (even? x)
               y (range 5)
               :when (odd? y)
               :let [z (* 2 y)]
               :when [(> 5 z)]]
           [x z]))
