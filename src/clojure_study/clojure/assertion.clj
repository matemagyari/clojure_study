(ns clojure-study.clojure.assertion)

(defn assert= [actual expected]
  (when-not (= actual expected)
    (throw 
      (AssertionError. 
        (str "Expected " expected " but was " actual)))))

(defn assert-false [expr] (assert (not expr)))   
(defn assert-nil [expr] (assert (nil? expr)))

