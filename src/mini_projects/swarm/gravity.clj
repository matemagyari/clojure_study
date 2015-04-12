(ns
  ^{:author mate.magyari}
  mini-projects.swarm.gravity
  (:require [clojure.test :as test]
            [mini-projects.swarm.vector-algebra :as va]))

(defn- gravity-vector
  "Gravity vector from point A to B. Direction depends on the points, the length
   is the square root of the distance between them and the gravitation constant"
  [point-from point-to grav-constant min-distance]
  (let [dist (Math/max
               min-distance
               (va/distance point-from point-to))]
    (if (= 0.0 dist)
      {:x 0.0 :y 0.0}
      (let [dir (va/direction-vector point-from point-to)
            multiplier (/ grav-constant (va/square dist))]
        (va/v* multiplier dir)))))

(defn sum-gravity-vector
  "Caclulates the total gravity force vector imposed on the subject-entity by the other entitites"
  [subject-entity entities global-constants]
  (let [g-map (:g-map subject-entity)
        to-force (fn [entity]
                   (gravity-vector
                     (:position subject-entity)
                     (:position entity)
                     (get g-map (:type entity))
                     (:min-proximity global-constants)))
        forces (pmap to-force entities)]
    (apply va/v+ forces)))

(def ^:private global-constants
  {:gravity-constants {:sheep {:sheep 1
                               :dead-sheep 0
                               :wolf -2
                               :wall -1}
                       :wolf {:sheep 2
                              :dead-sheep 0
                              :wolf 1
                              :wall -1}}
   :min-proximity 1.0})

(defn is= [a b]
  (test/is (= a b)))

(defn is-close-enough [vec-1 vec-2]
  (test/is (> 0.001
             (va/magnitude
               (merge-with - vec-1 vec-2)))))

(test/deftest some-tests
  (is= {:x 0.0 :y 0.0} (gravity-vector {:x 1 :y 1} {:x 1 :y 1} 5 1.0))
  (is= {:x 0.0 :y 2.5} (gravity-vector {:x 0 :y 0} {:x 0 :y 2} 10 1.0))
  (is= {:x -0.4 :y 0.0} (gravity-vector {:x 5 :y 2} {:x 0 :y 2} 10 1.0))
  )

(test/deftest sum-gravity-vector-test
  (let [center {:position {:x 0 :y 0}
                :g-map (get-in global-constants [:gravity-constants :sheep])
                :type :sheep}
        wolf-1 {:position {:x 2 :y 0}
                :g-map (get-in global-constants [:gravity-constants :wolf])
                :type :wolf}
        sheep-1 {:position {:x 2 :y 0}
                 :speed 10
                 :g-map (get-in global-constants [:gravity-constants :sheep])}]
    (is-close-enough {:x -0.25 :y 0.0} (sum-gravity-vector center [wolf-1 sheep-1] global-constants))
    (is-close-enough {:x -0.5 :y 0.0} (sum-gravity-vector center [wolf-1] global-constants))
    (is-close-enough {:x 0.25 :y 0.0} (sum-gravity-vector center [sheep-1] global-constants))))
