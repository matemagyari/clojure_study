(ns other.utils
  (:require [clojure.spec :as s]
            [clojure.spec.test :as stest]))

(defn collect [xs pred? f]
  (let [helper (fn [from to]
                 (if (empty? from)
                   to
                   (let [[head & tail] from
                         new-to (if (pred? head)
                                  (conj to (f head))
                                  to)]
                     (recur tail new-to))))]
    (helper xs [])))

(defn collect-first [xs pred? f]
  (if (empty? xs)
    nil
    (let [[head & tail] xs]
      (if (pred? head)
        (f head)
        (recur tail pred? f)))))

(defn find [pred? xs]
  (if (empty? xs)
    nil
    (let [[head & tail] xs]
      (if (pred? head)
        head
        (recur pred? tail)))))

;;this is the same as the built-in 'some'
(defn exists? [pred? xs]
  (if (empty? xs)
    false
    (let [[head & tail] xs]
      (if (pred? head)
        true
        (recur pred? tail)))))

;;this is the same as the built-in 'every?'
(defn forall? [pred? xs]
  (if (empty? xs)
    true
    (let [[head & tail] xs]
      (if (pred? head)
        (recur pred? tail)
        false))))

(collect (range 10) even? inc)
(collect-first (range 1 10) #(zero? (mod % 3)) inc)
(find even? [1 3 4 5 6])
(exists? even? [1 3 4 5 6])
(forall? even? [2 4 6])

;Option
(defn mapf [f x]
  (if x (f x)))

(defn forf [f & xs]
  (if (forall? some? xs) (apply f xs)))


