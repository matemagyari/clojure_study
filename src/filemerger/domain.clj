(ns filemerger.domain
  (:import [java.math BigDecimal])
  (:require [clojure.test :as test]
            [clojure.string :as str]))

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
            {:amount (* rate amount) :currency target-currency})))

(defn aggregate-transactions-of-partner
  "Aggregates the (potenatially lazy) list of transactions for a partner denominating in the target currency"
  [transactions partner target-currency exchange-rates]
  (as-> transactions $
    (filter #(= (:partner %) partner) $)
    (map #(convert-money % target-currency exchange-rates) $)
    (reduce +money {:amount 0 :currency target-currency} $)))


(defn add-transaction
  "Adds a transaction to the existing transactions map (key: partner, value: money)"
  [transactions {:keys [amount currency partner] :as transaction} target-currency exchange-rates]
  (let [current-value (get transactions partner {:amount 0 :currency target-currency})
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
  (is= {:currency :GBP :amount 11} (+money {:currency :GBP :amount 3} {:currency :GBP :amount 8})))

(test/deftest convert-money-tests
  (is= {:currency :chf :amount 6} (convert-money {:currency :GBP :amount 3} :chf {[:GBP :chf] 2}))
  (is= {:currency :chf :amount 6} (convert-money {:currency :chf :amount 6} :chf {})))

(test/deftest aggregate-transactions-of-partner-tests
  (let [exchange-rates {[:usd :GBP] 0.5}
        partner :x
        target-currency :GBP
        transactions [{:currency :GBP :amount 11 :partner :x}
                      {:currency :usd :amount 10 :partner :x}
                      {:currency :usd :amount 10 :partner :y}]]
    (is= {:currency :GBP :amount 16.0} (aggregate-transactions-of-partner transactions partner target-currency exchange-rates))))


(test/deftest aggregate-transactions-by-partner-tests
  (let [exchange-rates {[:usd :GBP] 0.5}
        target-currency :GBP
        transactions [{:currency :GBP :amount 11 :partner :x}
                      {:currency :usd :amount 10 :partner :x}
                      {:currency :usd :amount 10 :partner :y}]]
    (is= {:x {:currency :GBP :amount 16.0}
          :y {:currency :GBP :amount 5.0}} (aggregate-transactions-by-partner transactions target-currency exchange-rates))))

(test/run-tests)