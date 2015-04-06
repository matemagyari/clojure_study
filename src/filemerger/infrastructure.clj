(ns filemerger.infrastructure
  (:import [java.math BigDecimal]
           [java.io BufferedReader FileReader])
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [filemerger.domain :as d]
            [filemerger.app :as app]))


(defn ->number
  "Convert a number represented as a string to BigDecimal with given precision"
  [n]
  (let [bd-n (BigDecimal. n)
        scale (min 3 (.scale bd-n))]
    (.setScale bd-n scale BigDecimal/ROUND_HALF_DOWN)))

(defn csv-line->array
  "Splits the comma separated line"
  [line]
  (as-> line $ (.split $ ",") (map str/trim $)))

(defn line->transaction
  "Parses a line in the CSV file to a transaction"
  [line]
  (let [parts (csv-line->array line)
        partner (nth parts 0)
        currency (keyword (nth parts 1))
        amount (->number (nth parts 2))]
    {:currency currency :amount amount :partner partner}))

(defn line->exchange-rate
  "Parses a line in the CSV file to an exchange rate"
  [line]
  (let [parts (csv-line->array line)
        from-currency (keyword (nth parts 0))
        to-currency (keyword (nth parts 1))
        rate (->number (nth parts 2))]
    {:from from-currency :to to-currency :rate rate}))


(defn read-up-file
  "Reads up the lines from the file, processing them with line-fn and reducing the result into acc"
  [file-name reducing-fn]
  (with-open [rdr (BufferedReader. (FileReader. file-name))]
    (reducing-fn (line-seq rdr))))

(defn read-exchange-rates
  "Reads up exchange rates from file"
  [file-name]
  (let [reduce-fn (fn [rates line]
                    (let [{:keys [from to rate]} (line->exchange-rate line)]
                      (assoc rates [from to] rate)))
        line-seq->rates (fn [a-seq]
                          (reduce reduce-fn {} a-seq))]
    (read-up-file file-name line-seq->rates)))


(defn process-input
  "Processes the input and puts the results to the output"
  [{:keys [transactions-file exchange-rates-file target-currency partner] :as input}]
  (let [exchange-rates (read-exchange-rates exchange-rates-file)
        line-seq->transaction-seq #(map line->transaction %)
        aggregate-transactions-by-partner #(d/aggregate-transactions-by-partner (line-seq->transaction-seq %) target-currency exchange-rates)
        aggregate-transactions-of-partner #(d/aggregate-transactions-of-partner (line-seq->transaction-seq %) partner target-currency exchange-rates)
        result-by-partner (read-up-file transactions-file aggregate-transactions-by-partner)
        result-of-partner (read-up-file transactions-file aggregate-transactions-of-partner)]
    (println result-by-partner)
    (println result-of-partner)))


;; ========================= INFRASTRUCTURE TESTS ==============================
(defn is= [a b]
  (test/is (= a b)))

(test/deftest csv-line->array-tests
  (is= ["aa" "11" "rr"] (csv-line->array " aa , 11 , rr ")))

(test/deftest ->number-tests
  (is= (BigDecimal. "2.346") (->number "2.3456")))

(test/deftest line->exchange-rate-tests
  (is= {:from :GBP :to :USD :rate (->number "2.3")} (line->exchange-rate " GBP , USD , 2.3 ")))

(test/deftest line->transaction-tests
  (is= {:partner "HSBC" :currency :GBP :amount (->number "2.3")} (line->transaction " HSBC , GBP , 2.3 ")))

(test/run-tests)
