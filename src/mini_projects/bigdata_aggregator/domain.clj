(ns mini-projects.bigdata-aggregator.domain
  (:import [java.math BigDecimal])
  (:require [clojure.test :as test]
            [clojure.string :as str]))

(defrecord Transaction [amount currency partner])
(defrecord Money [amount currency])

(defn +money
  "Adds 2 'Money'-s"
  [money-1 {:keys [amount currency]}]
  {:pre [(= currency (:currency money-1))]}
  (update-in money-1 [:amount] + amount))

(defn convert-money
  "Converts a Money instance to the target-currency based on the exchange-rates"
  [{:keys [amount currency] :as money} target-currency exchange-rates]
  (cond
    (= target-currency currency) money
    :else (let [rate (get exchange-rates [currency target-currency])]
            (->Money (* rate amount) target-currency))))

(defn aggregate-transactions-of-partner
  "Aggregates the (potenatially lazy) list of transactions for a partner denominating in the target currency"
  [transactions partner target-currency exchange-rates]
  (as-> transactions $
    (filter #(= (:partner %) partner) $)
    (map #(convert-money % target-currency exchange-rates) $)
    (reduce +money (->Money 0 target-currency) $)))


(defn add-transaction
  "Adds a transaction to the existing transactions map (key: partner, value: money)"
  [transactions {:keys [amount currency partner] :as transaction} target-currency exchange-rates]
  (let [current-value (get transactions partner (->Money 0 target-currency))
        tran-value (convert-money transaction target-currency exchange-rates)
        new-value (+money current-value tran-value)]
    (assoc transactions partner new-value)))

(defn aggregate-transactions-by-partner
  "Aggregates the (potenatially lazy) list of transactions by partners denominating in the target currency"
  [transactions target-currency exchange-rates]
  (reduce #(add-transaction %1 %2 target-currency exchange-rates) {} transactions))


;; ========================= DOMAIN TESTS ==============================
(defn is= [a b]
  (test/is (= a b)))

(test/deftest +money-tests
  (is= (->Money 11 :GBP) (+money (->Money 3 :GBP) (->Money 8 :GBP))))

(test/deftest convert-money-tests
  (is= (->Money 6 :chf) (convert-money (->Money 3 :GBP) :chf {[:GBP :chf] 2}))
  (is= (->Money 6 :chf) (convert-money (->Money 6 :chf) :chf {})))

(test/deftest aggregate-transactions-of-partner-tests
  (let [exchange-rates {[:usd :GBP] 0.5}
        partner :x
        target-currency :GBP
        transactions [(->Transaction 11 :GBP :x)
                      (->Transaction 10 :usd :x)
                      (->Transaction 10 :usd :y)]]
    (is= (->Money 16.0 :GBP) (aggregate-transactions-of-partner transactions partner target-currency exchange-rates))))


(test/deftest aggregate-transactions-by-partner-tests
  (let [exchange-rates {[:usd :GBP] 0.5}
        target-currency :GBP
        transactions [(->Transaction 11 :GBP :x)
                      (->Transaction 10 :usd :x)
                      (->Transaction 10 :usd :y)]]
    (is= {:x (->Money 16.0 :GBP)
          :y (->Money 5.0 :GBP)} (aggregate-transactions-by-partner transactions target-currency exchange-rates))))

(test/run-tests)