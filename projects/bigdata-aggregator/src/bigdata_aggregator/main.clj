(ns bigdata-aggregator.main
  (:require [bigdata-aggregator.app :as app]))

;; main function - the app's entry point
(defn -main
  "The main function"
  [args]
  (let [result (app/process-input args)]
    (println "Result" (:result-of-partner result))))

;; TO RUN - prepare proper input
(def input {:exchange-rates-file "/Users/mate.magyari/Downloads/exchangerates.csv"
            :transactions-file "/Users/mate.magyari/Downloads/transactions/transactions.csv"
            :target-currency :GBP
            :partner "MRN8w"})

(def start (System/currentTimeMillis))
;(-main input)
(def end (System/currentTimeMillis))
(println (- end start))
