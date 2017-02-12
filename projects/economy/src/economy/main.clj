(ns economy.main
  (:require [economy.actor :as actor]
            [economy.trading :as trading]
            [clojure.spec :as s]
            [clojure.spec.test :as stest]))

(s/fdef create-actors
        :args empty?
        :ret (s/coll-of ::actor/actor))
(defn- create-actors []
  (let [number (* (count actor/commodity-types) 10)
        c (cycle actor/commodity-types)]
    (mapv actor/create-actor (range number) c)))


(s/def ::actors ::actor/actors)
(s/def ::transactions (s/coll-of ::actor/price))
(s/def ::time-to-trade (s/and nat-int? #(> 100 %)))

(s/def ::trade-result (s/keys :req [::actors ::transactions ::time-to-trade]))

(s/fdef trade
        :args (s/cat :input ::trade-result)
        :ret ::trade-result
        :fn (fn [x]
              (let [input (-> x :args :input)
                    output (:ret x)
                    input-actors (::actors input)
                    output-actors (::actors output)
                    all-goods (fn [actors]
                                (as-> actors $
                                      (map ::actor/commodities $)
                                      (reduce #(merge-with + %1 %2) {} $)))
                    all-money (fn [actors]
                                (as-> actors $
                                      (map ::actor/money $)
                                      (reduce + $)))
                    money-before (all-money input-actors)
                    money-after (all-money output-actors)]
                (if (not (= money-before money-after))
                  (println money-before money-after))
                (and
                  (if (zero? (::time-to-trade input))
                    (= input output)
                    true)
                  (<= (-> input ::transactions count) (-> output ::transactions count))
                  ;(= (all-money input-actors) (all-money output-actors))
                  (= (all-goods input-actors) (all-goods output-actors))
                  (= (set (map ::actor/id input-actors))
                     (set (map ::actor/id output-actors)))
                  ))))

(defn trade [{::keys [actors
                      transactions
                      time-to-trade] :as input}]
  (if (zero? time-to-trade)
    input
    (let [actor-in-need (some #(if (actor/in-need? %) %) actors)]
      (if actor-in-need
        (let [others (filterv #(not= % actor-in-need) actors)
              tr-result (trading/try-transaction actor-in-need others)]
          (if (-> tr-result :deal true?)
            (let [price (:price tr-result)
                  updated-buyer (get-in tr-result [:updates :buyer])
                  updated-seller (get-in tr-result [:updates :seller])
                  updated-actors (let [other-actor?
                                       (let [ids (->> [updated-seller updated-buyer]
                                                      (map ::actor/id)
                                                      (into #{}))]
                                         (fn [a] (not (contains? ids (::actor/id a)))))]
                                   (into [updated-buyer updated-seller] (filter other-actor? actors)))]
              (recur
                {::actors        updated-actors
                 ::transactions  (conj transactions price)
                 ::time-to-trade (dec time-to-trade)}))
            (recur
              {::actors        actors
               ::transactions  transactions
               ::time-to-trade (dec time-to-trade)})))
        input))))                                           ; every actor is content

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
                                        {::actors        actors
                                         ::transactions  []
                                         ::time-to-trade 200})]
    (doseq [a actors]
      (println "Comms1" (::actor/commodities a)))
    (println "Comms" (sum-commodities-quantity actors))
    (println "Actors" (count actors))
    (println transactions)
    (println "transactions number" (count transactions))))


;(main)

(defn adder
  "Returns random int in range start <= rand < end"
  [a b]
  (+ a (apply + b)))

(s/fdef adder
        :args (s/cat :a int? :b (s/coll-of (s/and int? #(< % 100) pos?) :kind vector? :count 2))
        :ret int?)



