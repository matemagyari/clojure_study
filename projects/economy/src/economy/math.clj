(ns economy.math
  "Basic match functions")

(defn output-delta
  "f(x+1)-f(x)"
  [{:keys [f x]}]
  (- (f (inc x)) (f x)))


(defn delta-conversion
  "y0+dy=f(x0+dx). Given y=f(x) function, its inverse, x0 and dy, it calculates dx.
  In plain english: how much should x increase to get dy increase in the output?"
  [{:keys [dy x0 f f-inv]}]
  ;if x0 is beyond a certain limit, there is no way to practically increase it
  (if (or (> x0 Double/MAX_VALUE)
          (zero? dy))
    0.0
    (let [y0 (f x0)
          x0+dx (f-inv (+ y0 dy))]
      ;if is beyond a certain limit, there is no way to practically increase it
      (if (> x0+dx Double/MAX_VALUE)
        Double/MAX_VALUE
        (- x0+dx x0)))))

(defn trunc
  "Truncates the number to 2 decimal places"
  [num]
  (Double/parseDouble (format "%.2f" num)))