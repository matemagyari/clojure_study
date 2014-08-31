(ns clojure-study.polimorph-defmulti
  (:require [clojure-study.assertion :as ae]))

;; defmulti based on a field
(defmulti area :shape)

(defmethod area :square [sq]
  (* (:side sq) (:side sq)))

(defmethod area :circle [circ]
  (* 3.14 (:rad circ) (:rad circ)))


(def a-square {:shape :square
               :side 10})
(def a-circle {:shape :circle
               :rad 10})

(ae/assert-equals 100 (area a-square))
(ae/assert-equals 314.0 (area a-circle))

;; defmulti with a default
(defmulti shape-name :shape)
(defmethod shape-name :rect [rec]
  "rectangle")
(defmethod shape-name :default [x]
  "no idea")
(ae/assert-equals "rectangle" (shape-name {:shape :rect}))
(ae/assert-equals "no idea" (shape-name {:a :b}))

;; defmulti based on a function

(defmulti measure
  (fn [x] (> 10 x)))

(defmethod measure true [_]
  "Less than 10")
(defmethod measure false [_]
  "Greater than 10")

(ae/assert-equals "Greater than 10" (measure 11))
(ae/assert-equals "Less than 10" (measure 9))


;; defmulti with multiple arguments
(defmulti even-x (fn [n m o] (* n 2)))

(defmethod even-x 4 [n m o]
  (+ n m o))
(defmethod even-x 6 [n m o]
  (* n m o))

(ae/assert-equals 9 (even-x 2 3 4))
(ae/assert-equals 36 (even-x 3 3 4))



(defmulti calculate (fn [op x y] (:type op)))

(defmethod calculate :plus [op x y]
  (+ x y))

(defmethod calculate :minus [op x y]
  (- x y))

(defmethod calculate :default [op x y]
  (* x y))

(ae/assert-equals 5 (calculate {:type :plus} 2 3))
(ae/assert-equals 3 (calculate {:type :minus} 7 4))
(ae/assert-equals 28 (calculate {:type :unknown} 7 4))