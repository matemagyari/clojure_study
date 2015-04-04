(ns filemerger.app
  (:import [java.math BigDecimal]
           [java.io BufferedReader FileReader])
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [filemerger.domain :as d]))

(defn process-transactions [{:keys [exchange-rates-file transactions-file target-currency] :as input}
                            transactions-file-reader
                            exchangerates-file-reader
                            ->output]
  (let [exchange-rates (exchangerates-file-reader exchange-rates-file)
        transactions (transactions-file-reader transactions-file)
        result (d/collapse transactions target-currency exchange-rates)]
    (->output result)))


(defn create-transactions-processor [transactions-file-reader exchangerates-file-reader ->output]
  (fn [input]
    (process-transactions input transactions-file-reader exchangerates-file-reader ->output)))





