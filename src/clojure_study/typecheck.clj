(ns clojure-study.typecheck
  (:use clojure-study.assertion))

(defn safe-str [^String x ^String y]
  (str x y))

(safe-str 1 3)

