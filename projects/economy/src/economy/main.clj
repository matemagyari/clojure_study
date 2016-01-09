(ns economy.main
  (:require [economy.actor :as actor]
            [economy.trading :as trading]))

(defn- create-actors []
  (let [number (* (count actor/commodity-types) 10)
        c (cycle actor/commodity-types)]
    (mapv actor/create-actor (range number) c)))

(defn- trade [{:keys [actors
                      transactions
                      time-to-trade] :as input}]
  (if (zero? time-to-trade)
    input
    (let [actor-in-need (some #(if (actor/in-need? %) %) actors)]
      (if actor-in-need
        (let [others (filterv #(not= % actor-in-need) actors)
              tr-result (trading/try-transaction actor-in-need others)]
          (if (true? (:deal tr-result))
            (let [price (:price tr-result)
                  buyer-index (.indexOf actors actor-in-need)
                  updated-buyer (get-in tr-result [:updates :buyer])
                  updated-seller (get-in tr-result [:updates :seller])
                  seller-index (.indexOf (mapv :id actors) (:id updated-seller))
                  updated-actors (assoc actors
                                   buyer-index updated-buyer
                                   seller-index updated-seller)]
              (recur
                {:actors                 updated-actors
                 :transactions           (conj transactions price)
                 :time-to-trade (dec time-to-trade)}))
            (recur
              {:actors                 actors
               :transactions           transactions
               :time-to-trade (dec time-to-trade)})))
        (do
          (println "All needs are satisfied")
          input)))))                                        ; every actor is content

(defn- sum-commodities-quantity
  "Sums up the quantity of commoditites of all actors"
  [actors]
  (->> actors
       (map actor/commodities-quantity)
       (reduce +)))

(defn main []
  (let [actors (create-actors)
        _ (println "Comms" (sum-commodities-quantity actors))
        {:keys [actors transactions]} (trade
                                        {:actors                 actors
                                         :transactions           []
                                         :time-to-trade 200})]
    (doseq [a actors]
      (println "Comms1" (:commodities a)))
    (println "Comms" (sum-commodities-quantity actors))
    (println "Actors" (count actors))
    (println transactions)
    (println "transactions number" (count transactions))))


(main)




