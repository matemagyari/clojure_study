(ns filemerger.domain
  (:import [java.math BigDecimal])
  (:require [clojure.test :as test]
            [clojure.string :as str]))

(defn add-transaction
  "Adds a transaction to the existing transactions map (key: currency, value: amount)"
  [transactions {amount :amount currency :currency}]
  (let [current-value (get transactions currency 0)
        new-value (+ amount current-value)]
    (assoc transactions currency new-value)))

(defn +money
  "Adds 2 'Money'-s"
  [money-1 {amount :amount currency :currency}]
  {:pre [(= currency (:currency money-1))]}
  (update-in money-1 [:amount] + amount))

(defn convert-money
  "Converts a Money instance to the target-currency based on the exchange-rates"
  [{:keys [amount currency] :as money} target-currency exchange-rates]
  (cond
    (= target-currency currency) money
    :else (let [rate (get exchange-rates [currency target-currency])]
            {:amount (* rate amount) :currency target-currency})))

(defn collapse
  "Collapse all transactions into one by converting all to the target-currency based on the exchange-rates"
  [transactions target-currency exchange-rates]
  (let [tuple->map (fn [[currency amount]]
                     {:currency currency :amount amount})]
    (as-> (sequence transactions) $
      (map #(convert-money (tuple->map %) target-currency exchange-rates) $)
      (reduce +money $))))


;; ========================= DOMAIN TESTS ==============================
(defn is= [a b]
  (test/is (= a b)))

(test/deftest add-transaction-tests
  (is= {:gdp 3} (add-transaction {} {:currency :gdp :amount 3}))
  (is= {:gdp 8} (add-transaction {:gdp 5} {:currency :gdp :amount 3}))
  (is= {:gdp 8 :eur 3} (add-transaction {:gdp 5 :eur 3} {:currency :gdp :amount 3})))

(test/deftest +money-tests
  (is= {:currency :gdp :amount 11} (+money {:currency :gdp :amount 3} {:currency :gdp :amount 8})))

(test/deftest convert-money-tests
  (is= {:currency :usd :amount 6} (convert-money {:currency :gdp :amount 3} :usd {[:gdp :usd] 2}))
  (is= {:currency :usd :amount 6} (convert-money {:currency :usd :amount 6} :usd {})))

(test/deftest collapse-tests
  (is= {:currency :usd :amount 16} (collapse {:gdp 3 :usd 10} :usd {[:gdp :usd] 2})))

(test/run-tests)