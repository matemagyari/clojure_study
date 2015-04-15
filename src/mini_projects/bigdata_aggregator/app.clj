(ns mini-projects.bigdata-aggregator.app
  (:import [java.math BigDecimal]
           [java.io BufferedReader FileReader])
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [mini-projects.bigdata-aggregator.domain :as d]
            [mini-projects.bigdata-aggregator.infrastructure :as i]))

(defn process-input
  "Processes the input and puts the results to the output"
  [{:keys [transactions-file exchange-rates-file target-currency partner] :as input}]
  (let [exchange-rates (i/read-exchange-rates exchange-rates-file)
        line-seq->transaction-seq #(map i/line->transaction %)
        aggregate-transactions-by-partner #(d/aggregate-transactions-by-partner (line-seq->transaction-seq %) target-currency exchange-rates)
        aggregate-transactions-of-partner #(d/aggregate-transactions-of-partner (line-seq->transaction-seq %) partner target-currency exchange-rates)
        result-by-partner (i/read-up-file transactions-file aggregate-transactions-by-partner)
        result-of-partner (i/read-up-file transactions-file aggregate-transactions-of-partner)]
    (println result-by-partner)
    (println result-of-partner)))





