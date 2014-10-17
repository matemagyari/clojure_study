(ns
  ^{:author mate.magyari
    :doc "Reading up the expedition config. Currently it's hard coded, but could come from file for example."}
  marsrovers.expedition-config-reader
  (:require [marsrovers.pure.rover :as r]))

;; -----------------  private functions ------------------------

(defn- rover-config [position actions]
  {:pre [(some? position) (some? actions)]}
  {:position position
   :actions actions
   :speed {:movement-speed 0
           :turning-speed 0}})

(defn- lot-of-actions
  "Creates a finite lazy sequence of rover actions"
  [n]
  (let [acc []]
    (if (zero? n)
      acc
      (cons (rand-nth [:left :move :right]) (lazy-seq (lot-of-actions (dec n)))))))

(defn- rand-rover-config [plateau-config]
  (let [x (rand-int (:x plateau-config))
        y (rand-int (:y plateau-config))
        facing (rand-nth [:n :w :s :e])]
    (rover-config
      (r/rover-position x y facing)
      (lot-of-actions 99999))))

(defn- rand-rover-configs [n plateau-config]
  (->> #(rand-rover-config plateau-config)
    repeatedly
    (take n)
    vec))

;; -----------------  public functions ------------------------

(defn expedition-config
  "Return the config for the expedition."
  []
  (let [plateau-config {:x 300 :y 300}]
    {:plateau-config plateau-config
     :rover-configs (rand-rover-configs 100 plateau-config)}))
