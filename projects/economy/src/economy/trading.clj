(ns economy.trading
  "Functions dealing with multiple actors"
  (:require [economy.actor :as actor]))

(defn- better-seller
  [{:keys [seller-1 price-offer-1 seller-2 commodity]}]
  (let [price-offer-2 (actor/min-selling-price {:commodity commodity
                                                :actor     seller-2})]
    (if (> price-offer-1 price-offer-2)
      {:seller seller-2 :price-offer price-offer-2}
      {:seller seller-1 :price-offer price-offer-1})))

(defn find-best-seller [{:keys [sellers commodity]}]
  (let [f (fn [{:keys [seller price-offer]} s2]
            (better-seller {:seller-1      seller
                            :price-offer-1 price-offer
                            :seller-2      s2
                            :commodity     commodity}))
        sellers (filter #(actor/has-it? % commodity) sellers)]
    (reduce f {:price-offer Integer/MAX_VALUE} sellers)))


(defn- do-transaction
  "Exchange money and commodity between 2 actors.
  Returns the updated actors"
  [{:keys [buyer seller commodity price]}]
  {:pre [(number? price) (keyword? commodity)]}
  (let [buyer (as-> buyer $
                    (update-in $ [:commodities commodity] inc)
                    (update-in $ [:money] - price))
        seller (as-> seller $
                     (update-in $ [:commodities commodity] dec)
                     (update-in $ [:money] + price))]
    {:buyer buyer :seller seller}))

(defn try-transaction
  "The buyer actor has a need. It makes the best deal available."
  [buyer sellers]
  (let [sought-commodity (first (for [[k v] (:commodities buyer) :when (zero? v)] k))
        {:keys [seller
                price-offer]} (find-best-seller
                                {:buyer     buyer
                                 :sellers   sellers
                                 :commodity sought-commodity})
        max-price (actor/max-acceptable-price {:commodity sought-commodity :actor buyer})]
    (if (>= max-price price-offer)
      {:deal    true
       :updates (do-transaction
                  {:buyer     buyer
                   :seller    seller
                   :commodity sought-commodity
                   :price     price-offer})
       :price   price-offer}
      {:deal false})))


