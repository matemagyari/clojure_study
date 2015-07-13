(ns puzzles.boat
  (:use clojure-study.assertion))

(defn safe? [x y]
  (case (set [x y])
      #{:wolf :sheep} false
      #{:sheep :cabbage} false
    true))

(defn move [from to p1 p2]
  [(disj from p1 p2)
   (conj to p1 p2)])

(defn finished? [side-a side-b]
  (= 3 (count side-a)))

(defn rand-2
  "Returns random 2 elements of a set"
  [s]
  (set
    (subvec (shuffle (vec s)) 0 2)))

(defn doit []
  (loop [side-a #{:wolf :sheep :cabbage}
         side-b #{}
         moves []
         boat-side side-a]
    (let [[p1 p2] (first (for [p1 boat-side
                               p2 boat-side
                               :when (and (not= p1 p2)
                                       (safe? p1 p2))]))
          [from to] (move boat-side to p1 p2)]
      ())))