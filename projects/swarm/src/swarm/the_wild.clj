(ns
  ^{:doc "Entities"}
  swarm.the-wild
  (:require [swarm.vector-algebra :as va]
            [clojure.test :as test]))

(defn enitites-of-type
  "Create entities of given type on random locations"
  [n traits entity-template global-constants dim-board rand-factor]
  (let [g-map (get-in global-constants [:gravity-constants (:type traits)])]
    (for [i (range n)
          :let [map-1 {:position {:x (int (* (rand-factor) (first dim-board)))
                                  :y (int (* (rand-factor) (second dim-board)))}
                       :g-map g-map}]]
      (merge entity-template map-1 traits))))


(defn- wall
  "Creates a Wall entity on the given location"
  [x y]
  {:position {:x x :y y}
   :type :wall
   :speed 0
   :stray 0})

(defn cull-sheeps
  "Kill the sheeps that are in the killing range of a wolf"
  [entities]
  (let [type-of? (fn [e t] (= t (:type e)))
        wolves (filter #(type-of? % :wolf) entities)
        close-enough-to-kill? (fn [wolf sheep]
                                (<=
                                  (va/distance (:position sheep) (:position wolf))
                                  (:kill-range wolf)))
        close-to-wolf? (fn [sheep]
                         (some #(close-enough-to-kill? % sheep) wolves))
        alter-e (fn [e]
                  (if (and
                        (type-of? e :sheep)
                        (close-to-wolf? e))
                    (assoc e :type :dead-sheep
                              :speed 0)
                    e))
        sheeps (filter type-of? :sheep)]
    (pmap alter-e entities)))

(defn make-walls
  "Create walls around the field"
  [dim-board]
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