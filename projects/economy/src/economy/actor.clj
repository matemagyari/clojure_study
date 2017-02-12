(ns economy.actor
  "Functions dealing with a single actor"
  (:require [economy.math :as math]
            [clojure.spec :as s]))

(def commodity-types #{:food :clothes :gas})

(s/def ::id (s/int-in 0 100))
(s/def ::product commodity-types)
(s/def ::commodities (s/map-of ::product (s/int-in 0 10) :count 3))
;how much is the subjective value of one unit
;of money compared to one unit of commodity
(s/def ::money-value-multiplier (s/double-in :min 0.1 :max 3.0 :NaN? false))
(s/def ::money (s/double-in :min 0.0 :NaN? false :infinite? false))
(s/def ::actor (s/keys :req [::id ::money-value-multiplier ::product ::money ::commodities]))
(s/def ::actors (s/and (s/coll-of ::actor)
                       (fn [actors]
                         (let [ids (map ::id actors)]
                           (= (count ids)
                              (count (distinct ids)))))))

(s/def ::price ::money)

(defn valid-price? [x]
  (if (s/valid? ::price x)
    true
    (do
      (println "Failed price [" x "]")
      false)))

(def zero-commodities
  (->> commodity-types
       (map (fn [c] [c 0]))
       (into {})))


(defn commodity-quantity->happiness
  "The subjective value of the quantity of commodity/money.
  quantity is increased so 0 doesn't yield minus infinity"
  [quantity]
  (-> quantity inc Math/log10))

;(defprotocol HappinessConverter
;  (happiness->money [this money-value-multiplier value] "...")
;  (money->happiness [this money-value-multiplier quantity] "..."))
;
;(def default-converter
;  (reify HappinessConverter
;    (happiness->money [this money-value-multiplier value]
;      (/ (dec (Math/pow 10 value))
;         money-value-multiplier))))

(defn happiness-value->commodity-quantity [value]
  (dec (Math/pow 10 value))
  (->> value (Math/pow 10) dec))

(defn money->happiness [money-value-multiplier money]
  (commodity-quantity->happiness (* money-value-multiplier money)))

(defn happiness->money [money-value-multiplier happiness]
  (/ (dec (Math/pow 10 happiness))
     money-value-multiplier))

(s/def ::commodity ::product)

(s/fdef worth-of-extra-unit
        :args (s/cat :worth (s/keys :req [::commodity ::actor]))
        :ret ::money)

(defn worth-of-extra-unit
  "The money equivalent of one unit of the given commodity.
  E.g. if the actor has 3 units of food, that is the happiness-delta
  between it's current state and that of a state where it has 2 units"
  [{::keys [commodity actor] :as input}]
  (let [happiness-gained (math/output-delta
                           {:f commodity-quantity->happiness
                            :x (get-in actor [::commodities commodity])})
        money-val-multiplier (::money-value-multiplier actor)
        worth (math/delta-conversion
                {:dy    happiness-gained
                 :x0    (::money actor)
                 :f     (partial
                          money->happiness money-val-multiplier)
                 :f-inv (partial
                          happiness->money money-val-multiplier)})]
    (if (not (s/valid? ::money worth))
      (println "AAA" worth happiness-gained (::money actor) input))
    worth))

(defn max-acceptable-price
  "The max price the actor should pay for an extra unit of the given commodity"
  [{:keys [commodity actor]}]
  (min (::money actor)
       (worth-of-extra-unit {::commodity commodity ::actor actor})))

(defn min-selling-price
  "The min price the actor should ask for a unit of the given commodity"
  [{:keys [commodity actor]}]
  {:post [(valid-price? %)]}
  (worth-of-extra-unit
    {::commodity commodity
     ::actor     (update-in actor [::commodities commodity] dec)}))

(defn in-need? [actor] (->> actor ::commodities vals (some zero?)))

(defn happiness
  "The happiness index of the actor calculated from its commodities,
  money and subjective value of money"
  [actor]
  (+ (money->happiness
       (::money-value-multiplier actor)
       (::money actor))
     (reduce + (map commodity-quantity->happiness (::commodities actor)))))

(defn commodities-quantity [actor]
  (reduce + (->> actor ::commodities vals)))

(defn has-it? [actor commodity]
  (pos? (get-in actor [::commodities commodity])))

(defn create-actor [id product]
  {::id                     id
   ::commodities            (assoc zero-commodities product 3)
   ::product                product
   ::money-value-multiplier (rand 3)
   ::money                  3})