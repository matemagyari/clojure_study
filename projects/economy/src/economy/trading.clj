(ns economy.trading
  "Functions dealing with multiple actors"
  (:require [economy.actor :as actor]
            [clojure.spec :as s]
            [clojure.spec.test :as stest]))

(s/def ::price-offer ::actor/money)
(s/def ::seller ::actor/actor)
(s/def ::offer (s/keys :req [::price-offer ::seller]))

(s/fdef find-best-seller
        :args (s/cat :sellers ::actor/actors
                     :commodity ::actor/product)
        :ret ::offer)
(defn better-seller
  [{:keys [seller-1 price-offer-1 seller-2 commodity]}]
  {:pre  [(actor/valid-price? price-offer-1)]
   :post [(actor/valid-price? (::price-offer %))]}
  (let [price-offer-2 (actor/min-selling-price {:commodity commodity :actor seller-2})]
    (if (> price-offer-1 price-offer-2)
      {::seller seller-2 ::price-offer price-offer-2}
      {::seller seller-1 ::price-offer price-offer-1})))

(s/fdef find-best-seller
        :args (s/cat :sellers ::actor/actors
                     :commodity ::actor/product)
        :ret (s/nilable ::offer))

(def counter (atom 0))

(defn find-best-seller [sellers commodity]
  (let [f (fn [{::keys [seller price-offer]} s2]
            (better-seller {:seller-1      seller
                            :price-offer-1 price-offer
                            :seller-2      s2
                            :commodity     commodity}))
        sellers (filter #(actor/has-it? % commodity) sellers)
        best (reduce f {::price-offer Double/MAX_VALUE} sellers)]
    (swap! counter inc)
    (if (some? (::seller best))
      best)))

(defn do-transaction
  "Exchange money and commodity between 2 actors.
  Returns the updated actors"
  [{:keys [buyer seller commodity price]}]
  {:pre [(actor/valid-price? price) (keyword? commodity)]}
  (let [buyer (as-> (s/conform ::actor/actor buyer) $
                    (update-in $ [::actor/commodities commodity] inc)
                    (update-in $ [::actor/money] - price))
        seller (as-> (s/conform ::actor/actor seller) $
                     (update-in $ [::actor/commodities commodity] dec)
                     (update-in $ [::actor/money] + price))]
    {:buyer buyer :seller seller}))


(s/fdef try-transaction
        :args (s/cat :buyer (s/and ::actor/actor
                                   actor/in-need?)
                     :sellers ::actor/actors)
        :ret map?)
(defn try-transaction
  "The buyer actor has a need. It makes the best deal available."
  [buyer sellers]
  (let [sought-commodity (first (for [[c num] (::actor/commodities buyer)
                                      :when (zero? num)]
                                  c))
        {::keys [seller price-offer]} (find-best-seller sellers sought-commodity)
        max-price (actor/max-acceptable-price {:commodity sought-commodity :actor buyer})]
    (if (and (some? seller)
             (>= max-price price-offer))
      {:deal    true
       :updates (do-transaction
                  {:buyer     buyer
                   :seller    seller
                   :commodity sought-commodity
                   :price     price-offer})
       :price   price-offer}
      {:deal false})))