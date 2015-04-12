(ns clojure_study.ideas.stream-duplication
  (:require [clojure.core.async :as async]
            [clojure.test :as test]))

(defn lazy-nums
  ([len] (lazy-nums len 1))
  ([len n]
    (println (str "Num:" n))
    (cond (> n len) []
      :else (cons n (lazy-seq (lazy-nums len (inc n)))))))

(def nums-seq (lazy-nums 20))
(println (class nums-seq))
(println (take 10 nums-seq))

(println (async/to-chan nums-seq))
