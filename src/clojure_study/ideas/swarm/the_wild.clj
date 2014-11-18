(ns
  ^{:author mate.magyari}
  clojure_study.ideas.swarm.the-wild
  (:require [clojure.test :as test]))

(defn enitites-of-type
  "Create entities of given type on random locations"
  [n traits entity-template global-constants dim-board]
  (let [g-map (get-in global-constants [:gravity-constants (:type traits)])]
    (for [i (range n)
          :let [map-1 {:position {:x (rand-int (first dim-board))
                                  :y (rand-int (second dim-board))}
                       :g-map g-map}]]
      (merge entity-template map-1 traits))))


(defn- wall
  "Creates a Wall entity on the given location"
  [x y]
  {:position {:x x :y y}
   :type :wall
   :speed 0
   :stray 0})

(defn make-walls [dim-board]
  (let [y-max (second dim-board)
        x-max (first dim-board)
        margin (int (/ x-max 100))
        vertical-walls (for [y (range y-max)
                             :when (zero? (mod y 5))]
                         [(wall margin y)
                          (wall (- x-max margin) y)])
        horizontal-walls (for [x (range x-max)
                               :when (zero? (mod x 5))]
                           [(wall x margin)
                            (wall x (- y-max margin))])]
    (flatten
      (concat vertical-walls horizontal-walls))))