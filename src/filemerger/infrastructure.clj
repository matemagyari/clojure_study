(ns filemerger.infrastructure
  (:import [java.math BigDecimal]
           [java.io BufferedReader FileReader])
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [filemerger.domain :as d]))


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
                  (d/add-transaction transactions (line->transaction line)))]
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

;; ========================= INFRASTRUCTURE TESTS ==============================
(defn is= [a b]
  (test/is (= a b)))

(test/deftest csv-line->array-tests
  (is= ["aa" "11" "rr"] (csv-line->array " aa , 11 , rr ")))

(test/deftest ->number-tests
  (is= (BigDecimal. "2.346") (->number "2.3456")))

(test/deftest line->exchange-rate-tests
  (is= {:from :gdp :to :usd :rate (->number "2.3")} (line->exchange-rate " GDP , USD , 2.3 ")))

(test/deftest line->transaction-tests
  (is= {:currency :gdp :amount (->number "2.3")} (line->transaction " GDP , 2.3 ")))

(test/run-tests)
