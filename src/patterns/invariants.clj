(ns
  ^{:author mate.magyari}
  patterns.invariants
  (:require [clojure-study.assertion :as a]))

(defn suites []
  [:heart :spade :diamond :club])

(defn ranks []
  [:2 :3 :4 :5 :6 :7 :8 :9 :10 :J :Q :K :A])

(defn new-deck []
  (for [s (suites)
        r (ranks)] [r s]))

(def a-deck (new-deck))
;;shuffle the deck
(shuffle (new-deck))
;;add cards on the top of the deck
(conj (new-deck) [:2 :spade] [:J :club])
;;pull the top card
([(peek a-deck) (pop a-deck)])

(defn is-valid-deck? [deck]
  (and (>= 52 (count deck))
    (distinct? deck)))

(defn is-full-deck? [deck]
  (= 52 (count deck)))

(defn with-invariant [invariant f in]
  {:pre [(invariant in)]
   :post [(invariant %)]}
  "Takes a validator function, a function and the input. Validates both
  the input and the output with the validator"
  (f in))

(a/assert-nil
  (try
    (with-invariant is-full-deck? rest (new-deck))
    (catch AssertionError err)))

(assert (with-invariant distinct? rest (new-deck)))

(defn with-valid-deck-invariant [f deck]
  (with-invariant is-valid-deck? f deck))

(assert (with-valid-deck-invariant rest (new-deck)))

(a/assert-nil
  (try
    (with-valid-deck-invariant #(cons [:2 :K] %) (new-deck))
    (catch AssertionError err)))

;(with-valid-deck-invariant #(conj % [:2 :spade]) (new-deck))

(defn is-valid-voter [p] (and (> (:age p) 18) (not (:in-prison p))))

(defn is-valid-voter [p]
  (and
    (> (:age p) 18)
    (not (:in-prison p))))

(defn wrap [x] [x])


