(ns clojure-study.clojure.deconstructing
  (:require clojure.contrib.core)
  (:use clojure-study.clojure.assertion))

;;----------------------------------------- DECONSTRUCTING VECTORS -------------------------------------------
;;vector
(let [[x y] [1 2]]
  (assert= x 1)
  (assert= y 2))

(let [[x & more] [1 2 3]]
  (assert= x 1)
  (assert= more [2 3]))

;;map
;;----------------------------------------- DECONSTRUCTING MAPS-------------------------------------------
(let [a-map {:weight 100 :height 180}
      {w :weight h :height} a-map]
  (assert= w 100)
  (assert= h 180))

(let [a-map {:p 1 :q 2}
      {:keys [p q z]} a-map]
  (assert= 1 p)
  (assert= 2 q)
  (assert= nil z))

;;provide default values
(defn sth [{:keys [age name]
            :as person
            :or {age 0
                 name "J. Doe"}}]
  [name age])

(assert= ["X" 1] (sth {:name "X" :age 1}))
(assert= ["J. Doe" 0] (sth {}))

;;let

(defn atest [a {b :x c :y}]
  (+ a b c))

(assert= (atest 3 {:x 4 :y 5}) 12)