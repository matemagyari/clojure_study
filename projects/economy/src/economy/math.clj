(ns economy.math
  "Basic match functions")

(defn output-delta
  "f(x+1)-f(x)"
  [{:keys [f x]}] (- (f (inc x)) (f x)))


(defn delta-conversion
  "y0+dy=f(x0+dx). Given y=f(x) function, its inverse, x0 and dy, it calculates dx.
  In plain english: how much should x increase to get dy increase in the output?"
  [{:keys [dy x0 f f-inv]}]
  (let [y0 (f x0)
        x0+dx (f-inv (+ y0 dy))]
    (- x0+dx x0)))