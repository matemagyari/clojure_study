(ns
  ^{:author mate.magyari
    :doc "Pure functions describing the rover movements"}
  marsrovers.pure.rover-move
  (:require [marsrovers.pure.util :as u]))

;facing can be :n :s :w :e

(defn is-valid-facing? [facing]
  (u/in? facing :n :w :s :e))

(defn turn-left [facing]
  {:pre [(is-valid-facing? facing)]}
  (condp = facing
    :n :w
    :w :s
    :s :e
    :e :n))

(defn turn-right [facing]
  {:pre [(is-valid-facing? facing)]}
  (condp = facing
    :n :e
    :e :s
    :s :w
    :w :n))


(defn move-x [facing]
  {:pre [(is-valid-facing? facing)]}
  (condp = facing
    :n 0
    :s 0
    :w -1
    :e 1))

(defn move-y [facing]
  {:pre [(is-valid-facing? facing)]}
  (condp = facing
    :n 1
    :s -1
    :w 0
    :e 0))
