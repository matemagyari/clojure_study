(ns
  ^{:author mate.magyari
    :doc "Simple vector algebra"}
  swarm.vector-algebra
  (:require [clojure.test :as test]
            [clojure.core.typed :as typed]))

;(typed/defalias Point '{:x typed/Num :y typed/Num})
(typed/defalias Point
  (typed/HMap :mandatory {:x Double, :y Double} :complete? true))
(typed/defalias CartesianVector
  (typed/HMap :mandatory {:x Double, :y Double} :complete? true))
(typed/defalias PolarVector
  (typed/HMap :mandatory {:angle Double, :magnitude Double} :complete? true))

;(typed/ann ^:no-check clojure.core/* [Double Double -> Double])
(typed/ann ^:no-check s* [Double Double -> Double])
(defn s* "Multiply "
  [x y]
  (* x y))

(typed/ann ^:no-check square [typed/Num -> Double])
(defn square "Square of"
  [num]
  (s* num num))


(typed/ann v* [Double CartesianVector -> CartesianVector])
(defn v* "Multiplication for vectors"
  [scalar a-vector]
  {:x (s* scalar (:x a-vector))
   :y (s* scalar (:y a-vector))})

(typed/ann ^:no-check v+ [CartesianVector * -> CartesianVector])
(defn v+ "Sum vector of vectors"
  [& vectors]
  (apply merge-with + vectors))

(typed/ann ^:no-check v- [CartesianVector CartesianVector -> CartesianVector])
(defn v- "Diff vector of vectors"
  [v1 v2]
  (merge-with - v1 v2))

(typed/ann magnitude [CartesianVector -> Double])
(defn magnitude [{x :x y :y}] "Length of a vector"
  (Math/sqrt
    (+ (square x) (square y))))

(typed/ann polar->cartesian [PolarVector -> CartesianVector])
(defn polar->cartesian
  "Transforming a polar vector representation to a cartesian one"
  [{angle :angle magnitude :magnitude}]
  {:x (s* magnitude (Math/cos angle))
   :y (s* magnitude (Math/sin angle))})

(typed/ann cartesian->polar [CartesianVector -> PolarVector])
(defn cartesian->polar
  "Transforming a cartesian vector representation to a polar one"
  [v]
  {:angle (Math/atan2 (:y v) (:x v))
   :magnitude (magnitude v)})

;(typed/ann ^:no-check clojure.core/update-in [CartesianVector CartesianVector -> CartesianVector])
(typed/ann ^:no-check rotate-cartesian [CartesianVector Double -> CartesianVector])
(defn rotate-cartesian "Rotating a vector"
  [v angle]
  (-> v
    cartesian->polar
    (update-in [:angle] + angle)
    polar->cartesian))

(typed/ann ^:no-check distance [Point Point -> Double])
(defn distance "Distance between 2 points"
  [point-from point-to]
  (magnitude
    (merge-with - point-to point-from)))

(typed/ann null-vector? [CartesianVector -> Boolean])
(defn null-vector? "Checks whether the vector is a null vector"
  [v]
  (or (= v {:x 0 :y 0})
    (= v {:x 0.0 :y 0.0})))

(defn normalize "Normalize the vector"
  [a-vector]
  (let [len (magnitude a-vector)]
    (if (= 0.0 len)
      a-vector ; null vector simply returned
      (let [div-len #(/ % len)]
        (-> a-vector
          (update-in [:x] div-len)
          (update-in [:y] div-len))))))

(defn direction-vector
  "(Normalized) direction vector from point A to point B"
  [point-from point-to]
  (normalize (merge-with - point-to point-from)))

(defn weight-point "Calculates the weight point of the points"
  [& points]
  (let [n (count points)
        sums (apply v+ points)]
    (-> sums
      (update-in [:x] #(/ % n))
      (update-in [:y] #(/ % n)))))


;;============== TESTS ==================
(defn is= [a b]
  (test/is (= a b)))

(defn is-close-enough [vec-1 vec-2]
  (test/is (> 0.001
             (magnitude
               (merge-with - vec-1 vec-2)))))

(test/deftest some-tests
  (is= 5.0 (magnitude {:x 4 :y 3}))
  (is= 5.0 (distance {:x 1 :y 3} {:x 4 :y 7}))
  (is= {:x 1.0 :y 0.0} (normalize {:x 4 :y 0}))
  (is= {:x 0.0 :y 1.0} (normalize {:x 0 :y 6}))
  (is-close-enough {:x (Math/sqrt 0.5) :y (Math/sqrt 0.5)} (normalize {:x 8 :y 8}))
  (is= {:x 0.0 :y 1.0} (direction-vector {:x 2 :y 6} {:x 2 :y 8}))
  (is= {:x -1.0 :y 0.0} (direction-vector {:x 8 :y 6} {:x 5 :y 6}))
  (is= {:x 15 :y 20} (v+ {:x 4 :y 6} {:x 6 :y 10} {:x 5 :y 4})))


(test/deftest cartesian->polar-test
  (is= {:angle 0.0, :magnitude 1.0} (cartesian->polar {:x 1 :y 0}))
  (is= {:angle Math/PI, :magnitude 1.0} (cartesian->polar {:x -1 :y 0}))
  (is-close-enough {:x 3 :y 7} (-> {:x 3 :y 7} cartesian->polar polar->cartesian)))

(test/deftest rotate-test
  (is-close-enough {:x 0 :y 1} (rotate-cartesian {:x 1 :y 0} (/ Math/PI 2)))
  (is-close-enough {:x -1 :y 0} (rotate-cartesian {:x 1 :y 0} Math/PI))
  (is-close-enough {:x 0 :y -1} (rotate-cartesian {:x -1 :y 0} (/ Math/PI 2)))
  (is-close-enough {:x 1 :y 0} (rotate-cartesian {:x -1 :y 0} Math/PI))
  )

(test/deftest weight-point-test
  (is-close-enough {:x 4 :y 6} (weight-point {:x 1 :y 4} {:x 7 :y 8})))


(test/run-tests 'swarm.vector-algebra)




;;(typed/check-ns 'swarm.vector-algebra)
