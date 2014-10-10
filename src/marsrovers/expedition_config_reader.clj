(ns
  ^{:author mate.magyari}
  marsrovers.expedition-config-reader
  (:require [marsrovers.pure.rover :as r]))

(def ^:private plateau-config {:x 200 :y 200})

(defn- rover-config [position actions]
  {:pre [(some? position) (some? actions)]}
  {:position position :actions actions})

(defn actions [n]
  (->> #(rand-nth [:left :move :right])
    repeatedly
    (take n)
    vec))

(defn- lot-of-actions []
  (actions 99999))

(defn- rand-config [plateau-config]
  (let [x (rand-nth (range (:x plateau-config)))
        y (rand-nth (range (:y plateau-config)))
        facing (rand-nth [:n :w :s :e])]
    (rover-config
      (r/rover-position x y facing)
      (lot-of-actions))))

(defn- rand-rover-configs [n plateau-config]
  (->> #(rand-config plateau-config)
    repeatedly
    (take n)
    vec))

(defn expedition-config []
  {:plateau-config plateau-config
   :rover-configs (rand-rover-configs 57 plateau-config)})
