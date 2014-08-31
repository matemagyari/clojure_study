(ns clojure-study.deconstructing
  (:require clojure.contrib.core)
  (:use clojure-study.assertion))

;;------------------------------------------------------------------------------- DECONSTRUCTING -------------------------------------------
;;vector
(let [[x y] [1 2]]
  (assert-equals x 1)
  (assert-equals y 2))

(let [[x & more] [1 2 3]]
  (assert-equals x 1)
  (assert-equals more [2 3]))

;;map
(let [a-map {:weight 100 :height 180}
      {w :weight h :height} a-map]
  (assert-equals w 100)
  (assert-equals h 180))

(let [a-map {:p 1 :q 2}
      {:keys [p q z]} a-map]
  (assert-equals 1 p)
  (assert-equals 2 q)
  (assert-equals nil z))

(defn atest [a {b :x c :y}]
  (+ a b c))

(assert-equals (atest 3 {:x 4 :y 5}) 12)