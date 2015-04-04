(ns filemerger.domain
  (:import [java.math BigDecimal]
           [java.io BufferedReader FileReader])
  (:require [clojure.test :as test]
            [clojure.string :as str]))

;; ========================= DOMAIN ==============================

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

;; ========================= APP ==============================
(defn evaluate-and->output
  [transactions-file exchange-rates-file target-currency]
  )
;; ========================= INFRASTRUCTURE ==============================

(defn ->number
  "Convert a number represented as a string to BigDecimal with given precision"
  [n]
  (let [bd-n (BigDecimal. n)
        scale (min 3 (.scale bd-n))]
    (.setScale bd-n scale BigDecimal/ROUND_HALF_DOWN)))

(defn csv-line->array
  "Splits the comma separated line"
  [line]
  (as-> line $ (.toLowerCase $) (.split $ ",") (map str/trim $)))

(defn read-file
  "Reads up the lines from the file, processing them with line-fn and reducing the result into acc"
  [file-name line-fn acc]
  (with-open [rdr (BufferedReader. (FileReader. file-name))]
    (reduce line-fn acc (line-seq rdr))))

(defn line->transaction
  "Parses a line in the CSV file to a transaction"
  [line]
  (let [parts (csv-line->array line)
        currency (keyword (first parts))
        amount (BigDecimal. (second parts))]
    {:currency currency :amount amount}))

(defn read-transactions
  "Reads up transactions from file"
  [file-name]
  (let [line-fn (fn [transactions line]
                  (add-transaction transactions (line->transaction line)))]
    (read-file file-name line-fn {})))

(defn line->exchange-rate
  "Parses a line in the CSV file to an exchange rate"
  [line]
  (let [parts (csv-line->array line)
        from-currency (keyword (nth parts 0))
        to-currency (keyword (nth parts 1))
        rate (BigDecimal. (nth parts 2))]
    {:from from-currency :to to-currency :rate rate}))

(defn read-exchange-rates
  "Reads up exchange rates from file"
  [file-name]
  (let [line-fn (fn [rates line]
                  (let [{from :from to :to rate :rate} (line->exchange-rate line)]
                    (assoc rates [from to] rate)))]
    (read-file file-name line-fn {})))

(def start (System/currentTimeMillis))
;(read-transactions "/Users/mate.magyari/IdeaProjects/ScalaStudy/transactions.csv")
(println
  (- (System/currentTimeMillis) start))
(println (read-exchange-rates "/Users/mate.magyari/IdeaProjects/ScalaStudy/exchangerates.csv"))

(println (.setScale (BigDecimal. "1.23455") 3 BigDecimal/ROUND_HALF_DOWN))

;; ========================== TESTS ==============================
;; ===============================================================

(defn is= [a b]
  (test/is (= a b)))

;; ========================= DOMAIN TESTS ==============================

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

;; ========================= INFRASTRUCTURE TESTS ==============================

(test/deftest csv-line->array-tests
  (is= ["aa" "11" "rr"] (csv-line->array " aa , 11 , rr ")))

(test/deftest ->number-tests
  (is= (BigDecimal. "2.346") (->number "2.3456")))

(test/deftest line->exchange-rate-tests
  (is= {:from :gdp :to :usd :rate (->number "2.3")} (line->exchange-rate " GDP , USD , 2.3 ")))

(test/deftest line->transaction-tests
  (is= {:currency :gdp :amount (->number "2.3")} (line->transaction " GDP , 2.3 ")))

(test/run-tests)