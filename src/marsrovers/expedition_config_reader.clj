(ns
  ^{:author mate.magyari
    :doc "Reading up the expedition config. Currently it's hard coded, but could come from file for example."}
  marsrovers.expedition-config-reader
  (:require [marsrovers.pure.rover :as r]))

;; -----------------  private functions ------------------------

(def ^:private plateau-config {:x 300 :y 300})

(defn- rover-config [position actions]
  {:pre [(some? position) (some? actions)]}
  {:position position
   :actions actions
   :speed {:movement-speed 0
           :turning-speed 0}})

(defn- actions [n]
  (->> #(rand-nth [:left :move :right])
    repeatedly
    (take n)
    vec))

(defn- lot-of-actions []
  (actions 9999))

(defn lot-of-actions-2 [n]
  (let [acc []]
    (if (zero? n)
      acc
      (cons (rand-nth [:left :move :right]) (lazy-seq (lot-of-actions-2 (dec n)))))))

(defn- rand-config [plateau-config]
  (let [x (rand-int (:x plateau-config))
        y (rand-int (:y plateau-config))
        facing (rand-nth [:n :w :s :e])]
    (rover-config
      (r/rover-position x y facing)
      (lot-of-actions))))

(defn- rand-rover-configs [n plateau-config]
  (->> #(rand-config plateau-config)
    repeatedly
    (take n)
    vec))

;; -----------------  public functions ------------------------

(defn expedition-config
  "Return the config for the expedition."
  []
  {:plateau-config plateau-config
   :rover-configs (rand-rover-configs 100 plateau-config)})
