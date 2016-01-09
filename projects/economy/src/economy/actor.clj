(ns economy.actor
  "Functions dealing with a single actor"
  (:require [economy.math :as math]))

(def zero-commodities {:food 0 :clothes 0 :gas 0})
(def commodity-types (keys zero-commodities))

(defn commodity-quantity->happiness
  "The subjective value of the quantity of commodity/money.
  quantity is increased so 0 doesn't yield minus infinity"
  [quantity] (Math/log10 (inc quantity)))

;(defprotocol HappinessConverter
;  (happiness->money [this money-value-multiplier value] "...")
;  (money->happiness [this money-value-multiplier quantity] "..."))
;
;(def default-converter
;  (reify HappinessConverter
;    (happiness->money [this money-value-multiplier value]
;      (/ (dec (Math/pow 10 value))
;         money-value-multiplier))))

(defn happiness-value->commodity-quantity
  [value] (dec (Math/pow 10 value)))

(defn money->happiness [money-value-multiplier money]
  (commodity-quantity->happiness (* money-value-multiplier
                                          money)))

(defn happiness->money [money-value-multiplier happiness]
  (/ (dec (Math/pow 10 happiness))
     money-value-multiplier))

(defn worth-of-extra-unit
  "The money equivalent of one unit of the given commodity.
  E.g. if the actor has 3 units of food, that is the happiness-delta
  between it's current state and that of a state where it has 2 units"
  [{:keys [commodity
           actor]}]
  (let [happiness-gained (math/output-delta
                           {:f commodity-quantity->happiness
                            :x (get-in actor [:commodities commodity])})]
    (math/delta-conversion
      {:dy    happiness-gained
       :x0    (:money actor)
       :f     (partial
                money->happiness
                (:money-value-multiplier actor))
       :f-inv (partial
                happiness->money
                (:money-value-multiplier actor))})))

(defn max-acceptable-price
  "The max price the actor should pay for an extra unit of the given commodity"
  [{:keys [commodity
           actor]}]
  (min (:money actor)
       (worth-of-extra-unit
         {:commodity commodity
          :actor     actor})))

(defn min-selling-price
  "The min price the actor should ask for a unit of the given commodity"
  [{:keys [commodity
           actor]}]
  (worth-of-extra-unit
    {:commodity commodity
     :actor     (update-in actor [:commodities commodity] dec)}))

(defn in-need? [actor] (->> actor :commodities vals (some zero?)))

(defn happiness
  "The happiness index of the actor calculated from its commodities,
  money and subjective value of money"
  [actor]
  (+ (money->happiness
       (:money-value-multiplier actor)
       (:money actor))
     (reduce + (map commodity-quantity->happiness (:commodities actor)))))

(defn commodities-quantity [actor]
  (reduce + (->> actor :commodities vals)))

(defn has-it? [actor commodity]
  (pos? (get-in actor [:commodities commodity])))

(defrecord Actor [id
                  commodities
                  produces
                  ;how much is the subjective value of one unit
                  ;of money compared to one unit of commodity
                  money-value-multiplier
                  money])

(defn create-actor [id product]
  (map->Actor
    {:id                     id
     :commodities            (assoc zero-commodities product 3)
     :produces               product
     :money-value-multiplier (rand 3)
     :money                  3}))