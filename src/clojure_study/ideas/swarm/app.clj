(ns
  ^{:author mate.magyari}
  clojure_study.ideas.swarm.app
  (:require [clojure.test :as test]
            [clojure-study.ideas.swarm.swarm :as swarm]
            [clojure-study.ideas.swarm.vector-algebra :as va]
            [clojure-study.ideas.swarm.display :as display]))



(defn entity->view
  "Convert an entity to a view - a tuple"
  [entity]
  (let [position (get entity :position)]
    [(:type entity)
     (-> position :x int)
     (-> position :y int)]))

(defn entities->view
  "Converts a sequence of entitites to views, grouped by rows"
  [entities]
  (let [e-views (map entity->view entities)
        groups (group-by second e-views)]
    (vals groups)))

(def global-constants
  {:gravity-constants {:sheep {:sheep 60
                               :wolf -60
                               :wall -200}
                       :wolf {:sheep 30
                              :wolf 1
                              :wall -200}
                       :wall {:sheep 0
                              :wolf 0
                              :wall 0}}
   :min-proximity 1.0})

(def entity-template
  {:position {:x 2 :y 0}
   :speed 3
   :stray (/ Math/PI 32)
   :g-map (get-in global-constants [:gravity-constants :sheep])
   :type :sheep})

(def dim-board [200 200])
(def dim-screen [400 400])
(def repaint! (display/repaint-fn dim-board dim-screen))

(defn enitites-of-type
  "Create entities of given type on random locations"
  [n e-type dim-board]
  (for [i (range n)]
    (assoc entity-template
      :position {:x (rand-int (first dim-board))
                 :y (rand-int (second dim-board))}
      :type e-type
      :g-map (get-in global-constants [:gravity-constants e-type]))))


(defn wall [x y]
  (assoc entity-template
    :position {:x x :y y}
    :type :wall :speed 0
    :stray 0))

(def walls
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

(def sheeps (enitites-of-type 100 :sheep dim-board))
(def wolves (enitites-of-type 10 :wolf dim-board))

(time
  (loop [i 500
         entities (concat sheeps wolves walls)]
    (if (zero? i)
      (println "END")
      (let [entitites-next (swarm/next-positions entities global-constants)]
        (println "hi " i " " (apply va/weight-point (map :position entitites-next)))
        ;(Thread/sleep 10)
        (repaint! (entities->view entitites-next))
        (recur (dec i) entitites-next)))))
