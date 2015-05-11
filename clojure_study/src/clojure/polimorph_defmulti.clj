(ns clojure.polimorph-defmulti
  (:require [clojure-study.clojure.assertion :as ae]))


;; defmulti and defmethod signatures
;; (defmulti method-name dispatcher-function)
;; (defmethod method-name dispatcher-value)

;; defmulti based on a field - the dispatching function is :shape (which works as a function on maps)
(defmulti area :shape)

(defmethod area :square [sq]
  (* (:side sq) (:side sq)))

(defmethod area :circle [circ]
  (* 3.14 (:rad circ) (:rad circ)))

;; default option
(defmethod area :default [_] :no-idea)


(ae/assert= 100 (area {:shape :square :side 10}))
(ae/assert= 314.0 (area {:shape :circle :rad 10}))
(ae/assert= :no-idea (area {:shape :deltoid}))

;; defmulti with a 'real' dispatcher funtion

(defmulti measure
  (fn [x] (> 10 x)))

(defmethod measure true [_]
  "Less than 10")
(defmethod measure false [_]
  "Greater than 10")

(ae/assert= "Greater than 10" (measure 11))
(ae/assert= "Less than 10" (measure 9))


;; defmulti with multiple arguments

(defmulti calculate (fn [op x y] (:type op)))

(defmethod calculate :plus [op x y]
  (+ x y))

(defmethod calculate :minus [op x y]
  (- x y))

(defmethod calculate :default [op x y]
  (* x y))

(ae/assert= 5 (calculate {:type :plus} 2 3))
(ae/assert= 3 (calculate {:type :minus} 7 4))
(ae/assert= 28 (calculate {:type :unknown} 7 4))