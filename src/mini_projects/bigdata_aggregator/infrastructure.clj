(ns mini-projects.bigdata-aggregator.infrastructure
  (:import [java.math BigDecimal]
           [java.io BufferedReader FileReader])
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [mini-projects.bigdata-aggregator.domain :as d]))


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
    (d/->Transaction amount currency partner)))

(defn line->exchange-rate
  "Parses a line in the CSV file to an exchange rate"
  [line]
  (let [parts (csv-line->array line)
        from-currency (keyword (nth parts 0))
        to-currency (keyword (nth parts 1))
        rate (->number (nth parts 2))]
    {:from from-currency :to to-currency :rate rate}))


(defn reduce-file-line-by-line
  "Reads up the lines from the file, reducing them with reducing-fn"
  [file-name reducing-fn]
  (with-open [rdr (io/reader file-name)]
    (reducing-fn (line-seq rdr))))

(defn read-exchange-rates
  "Reads up exchange rates from file"
  [file-name]
  (let [reduce-fn (fn [rates line]
                    (let [{:keys [from to rate]} (line->exchange-rate line)]
                      (assoc rates [from to] rate)))
        line-seq->rates (fn [a-seq]
                          (reduce reduce-fn {} a-seq))]
    (reduce-file-line-by-line file-name line-seq->rates)))



;; ========================= TESTS ==============================
(defn is= [a b]
  (test/is (= a b)))

(test/deftest csv-line->array-tests
  (is= ["aa" "11" "rr"] (csv-line->array " aa , 11 , rr ")))

(test/deftest ->number-tests
  (is= (BigDecimal. "2.346") (->number "2.3456")))

(test/deftest line->exchange-rate-tests
  (is= {:from :GBP :to :USD :rate (->number "2.3")} (line->exchange-rate " GBP , USD , 2.3 ")))

(test/deftest line->transaction-tests
  (is= (d/->Transaction (->number "2.3") :GBP "HSBC") (line->transaction " HSBC , GBP , 2.3 ")))

;(test/run-tests)
