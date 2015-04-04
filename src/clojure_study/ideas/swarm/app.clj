(ns
  ^{:author mate.magyari}
  clojure-study.ideas.swarm.app
  (:require [clojure.test :as test]
            [clojure-study.ideas.swarm.swarm :as swarm]
            [clojure-study.ideas.swarm.the-wild :as w]
            [clojure-study.ideas.swarm.display :as display]
            [clojure-study.ideas.swarm.view-adapter :as v]
            [clojure.core.typed :as typed]))

(def global-constants
  {:gravity-constants {:sheep {:sheep 1
                               :wolf -10
                               :dead-sheep 0
                               :wall -20}
                       :wolf {:sheep 3
                              :wolf -1
                              :dead-sheep 0
                              :wall -20}
                       :wall {:sheep 0
                              :wolf 0
                              :dead-sheep 0
                              :wall 0}}
   :min-proximity 1.0})

(def entity-template
  {:position nil
   :speed 3
   :stray-tendency {:random-max (/ Math/PI 100) ; defines the angle the entity can deviate from the gravitational vector
                    :constant (/ Math/PI 8)}
   :g-map nil
   :type nil})

(defn rand-factor-creator
  "Produces a psesudo-random number generator function"
  []
  (let [last (atom Math/PI)]
    (fn []
      (let [n1 (* 13 @last)
            n2 (- n1 (int n1))]
        (reset! last n2)
        n2))))

(def rand-factor (rand-factor-creator))
(def rand-factor rand)

(defn run-show!
  "Runs the simulation"
  [sheeps-num wolves-num dim-board dim-screen]
  (let [wolf-traits {:type :wolf :kill-range 1}
        sheep-traits {:type :sheep}
        sheeps (w/enitites-of-type sheeps-num sheep-traits entity-template global-constants dim-board rand-factor)
        wolves (w/enitites-of-type wolves-num wolf-traits entity-template global-constants dim-board rand-factor)
        walls (w/make-walls dim-board)
        repaint! (display/repaint-fn dim-board dim-screen)]
    (println "START")
    (loop [i 5000
           entities (concat sheeps wolves walls)]
      (if (zero? i)
        (println "END")
        (let [entitites-next (swarm/next-positions entities global-constants rand-factor)
              entitites-next (w/cull-sheeps entitites-next)
              ]
          ;(println "hi " i " " (apply va/weight-point (map :position entitites-next)))
          (repaint! (v/entities->view entitites-next))
          (recur (dec i) entitites-next))))))

;(run-show! 100 10 [200 200] [400 400])

(typed/check-ns 'clojure-study.ideas.swarm.vector-algebra)
