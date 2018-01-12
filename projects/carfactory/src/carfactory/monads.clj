(ns carfactory.monads
  (:require [clojure.core.match :as m]))

(defn find-car [vrm cars]
  (let [r (some #(if (= (:vrm %) vrm) %) cars)]
    (if r {:right r} {:left vrm})))

(defn combine-2 [m1 m2 f]
  (m/match [m1 m2]
           [{:left _} _] m1
           [_ {:left _}] m2
           :else {:right (f (:right m1) (:right m2))}))

(defn combine [f ms]
  (reduce #(combine-2 %1 %2 f) (first ms) (rest ms)))

(defn compare-prices-2 [cars f vrm-1 vrm-2]
  (let [car-1 (find-car vrm-1 cars)
        car-2 (find-car vrm-2 cars)]
    (combine f [car-1 car-2])))

(def cars [{:vrm "vrm1" :price 10}
           {:vrm "vrm2" :price 20}])