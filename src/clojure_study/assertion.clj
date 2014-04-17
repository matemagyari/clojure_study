(ns clojure-study.assertion)

(defn assert-equals [actual expected]
  (when-not (= actual expected)
    (throw 
      (AssertionError. 
        (str "Expected " expected " but was " actual)))))

(defn assert-false [expr] (assert (not expr)))   

