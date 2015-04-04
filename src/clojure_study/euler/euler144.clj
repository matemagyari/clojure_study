(ns
  ^{:author mate.magyari}
  clojure_study.euler.euler144
  (:require [clojure.test :as test]))


(defn line-from-slope-and-point
  "Calculate [a b] where y = ax+b. [x0 y0] is a point on the line
   'a' is slope"
  [a [x0 y0]]
  (let [b (- y0
            (* a x0))]
    [a b]))

(defn line-from-2-points
  "Calculate line from two point"
  [[x0 y0] [x1 y1]]
  (let [slope (/ (- y1 y0) (- x1 x0))]
    (line-from-slope-and-point slope [x0 y0])))

(defn line-intersection
  "Line 1 is ax+c
   Line 2 is bx+d"
  [[a c] [b d]]
  (let [x (/
            (- d c)
            (- a b))
        y (+ (* a x) c)]
    [x y]))

;; 4x2 + 4y2 = 100
;; y = ax + b
;;4x2 + 4(ax+b)2 = 100
;;x2 + a2x2 + 2 abx + b2 = 25
;;(a2+1)x2 + 2abx + b2 = 25
;;x = (-2ab + sqrt())

(defn intersection-points
  "Calculates the point(s) of intersection between an ellipse and a line
   Ellipse's equation is 4 * x^2 + 4 * y^2 = 100
   Line's is y = ax + b, where a and b are parameters"
  [line]
  1)

(defn tangent-slope
  "Tangent slope on a given point"
  [[x y]]
  (- (/ (* 4 x) y)))

(defn tangent-line [point]
  (line-from-slope-and-point (tangent-slope point) point))

(defn normal-line [tangent-point])

(defn exit-point? [[x y]]
  (and (pos? y)
    (>= 0.01 (Math/abs x))))

(defn new-course
  [start-point beam-line]
  (let [[p1 p2] (intersection-points beam-line)
        end-point (if (= p1 start-point) p2 p1)]
    ))

(def solution
  (let [start-line (line-from-2-points [0.0 10.1] [1.4 -9.6])]
    (loop [start-point [0.0 10.1]
           end-point [1.4 -9.6]
           bounce 0]
      (let [[point-1 point-2] (intersection-points line)]
        (if (and (pos? bounce) (exit-point? end-point))
          bounce
          (let []))))))
