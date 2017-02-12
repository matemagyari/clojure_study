(ns
  ^{:doc "Swarm dynamic, based on gravitational forces between entitites"}
  swarm.swarm
  (:require [clojure.test :as test]
            [swarm.vector-algebra :as va]
            [swarm.gravity :as g]))


(defn deviation
  "Random double in [-max-deviaton +max-deviaton]"
  [stray-tendency rand-num]
  (let [random-max (:random-max stray-tendency)
        random (- (* 2 random-max rand-num) random-max)]
    (+ (:constant stray-tendency) random)))

(defn next-position
  "Calculation of the next position based on the entities around and a random element"
  [global-constants entity entities rand-num]
  (let [speed (:speed entity)
        position (:position entity)]
    (cond (pos? speed) ;; don't bother with entities with zero speed
      (let [total-g-vector (g/sum-gravity-vector entity entities global-constants)
            adjusted-dir-vector (va/rotate-cartesian total-g-vector (deviation (:stray-tendency entity) rand-num))
            result-vector (va/v* speed (va/normalize adjusted-dir-vector))]
        ;(println "DEV" total-g-vector " " adjusted-dir-vector)
        (va/v+ (:position entity) result-vector))
      :else position)))

(defn next-positions
  "Calculate the next positions of the entitites"
  [entitites global-constants rand-factor]
  (let [indexed-entities (map-indexed vector entitites)
        rand-nums (vec
                    (take (count indexed-entities)
                      (repeatedly rand-factor)))]
    (for [ie indexed-entities
          :let [entity (second ie)
                others-transd (comp
                                (remove #(= ie %))
                                (map second))
                others (sequence others-transd indexed-entities)
                rand-num (nth rand-nums (first ie))
                next-pos (next-position global-constants entity others rand-num)]]
      (assoc entity :position next-pos))))


(defn is= [a b]
  (test/is (= a b)))

(defn is-close-enough [vec-1 vec-2]
  (test/is (> 0.001
             (va/magnitude
               (merge-with - vec-1 vec-2)))))


(def global-constants
  {:gravity-constants {:sheep {:sheep 1
                               :wolf -2
                               :wall -1}
                       :wolf {:sheep 2
                              :wolf 1
                              :wall -1}}
   :min-proximity 1.0})

(test/deftest deviation-test
  (is= 3.0 (deviation {:random-max 10.0 :constant 3} 0.5))
  (is= 13.0 (deviation {:random-max 10.0 :constant 3} 1))
  (is= -7.0 (deviation {:random-max 10.0 :constant 3} 0.0))
  )

(test/deftest next-positions-test
  (let [sheep-1 {:position {:x 0 :y 0}
                 :speed 10
                 :g-map (get-in global-constants [:gravity-constants :sheep])
                 :type :sheep}
        sheep-2 {:position {:x 0 :y 0}
                 :speed 10
                 :g-map (get-in global-constants [:gravity-constants :sheep])
                 :type :sheep}]
    ))

;(test/run-tests 'swarm.swarm)
