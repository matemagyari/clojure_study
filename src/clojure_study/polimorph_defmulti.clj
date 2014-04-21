(ns clojure-study.polimorph-defmulti
  (:use clojure-study.assertion))

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

(assert-equals 100 (area a-square))
(assert-equals 314.0 (area a-circle))

;; defmulti with a default
(defmulti shape-name :shape)
(defmethod shape-name :rect [rec]
  "rectangle")
(defmethod shape-name :default [x]
  "no idea")
(assert-equals "rectangle" (shape-name {:shape :rect}))
(assert-equals "no idea" (shape-name {:a :b}))

;; defmulti based on a function

(defmulti measure
  (fn [x] (> 10 x)))

(defmethod measure true [_]
  "Less than 10")
(defmethod measure false [_]
  "Greater than 10")

(assert-equals "Greater than 10" (measure 11))
(assert-equals "Less than 10" (measure 9))