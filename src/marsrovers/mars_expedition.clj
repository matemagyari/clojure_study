(ns
  ^{:author mate.magyari}
  marsrovers.mars-expedition
  (:require [clojure.core.async :as a]
            [clojure.core.match :as m]
            [marsrovers.api.rover-api :as api]
            [marsrovers.pure.plateau :as p]
            [marsrovers.pure.rover :as r]
            [marsrovers.pure.nasa-hq :as hq]
            [marsrovers.app.app :as app]))

(def plateau-config {:x 200 :y 200})
(defn- rover-config [position actions]
  {:pre [(some? position) (some? actions)]}
  {:position position :actions actions})

(defn actions [n]
  (->> #(rand-nth [:left :move :right])
    repeatedly
    (take n)
    vec))

(defn lot-of-actions []
  (actions 200))

(defn rand-config [plateau-config]
  (let [x (rand-nth (range (:x plateau-config)))
        y (rand-nth (range (:y plateau-config)))
        facing (rand-nth [:n :w :s :e])]
    (rover-config
      (r/rover-position x y facing)
      (lot-of-actions))))

(defn rand-rover-configs [n plateau-config]
  (->> #(rand-config plateau-config)
    repeatedly
    (take n)
    vec))


(def rover-configs (rand-rover-configs 10 plateau-config))

(def expedition-config {:plateau-config plateau-config
                        :rover-configs rover-configs})

(def plateau-atom (atom
                    (p/plateau plateau-config)))

(def nasa-hq-atom (atom
                    (hq/nasa-hq expedition-config)))


(defn start-rovers! [n plateau-channel mediator-channel]
  (let [rover-atoms (for [i (range n)]
                      (atom
                        (r/rover i (a/chan 10))))]
    (doseq [rover-atom rover-atoms]
      (app/start-rover! rover-atom plateau-channel mediator-channel))))

(app/start-nasa-hq! nasa-hq-atom)
(app/start-plateau! plateau-atom)

(start-rovers! (count rover-configs) (:in-channel @plateau-atom) (:in-channel @nasa-hq-atom))

(a/<!! (a/timeout 1000))


