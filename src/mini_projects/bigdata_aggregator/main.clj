(ns mini-projects.bigdata-aggregator.main
  (:require [mini-projects.bigdata-aggregator.app :as app]))

;; main function - the app's entry point
(defn -main
  "The main function"
  [args]
  (app/process-input args))


;; TO RUN - prepare proper input
(def input {:exchange-rates-file "exchangerates.csv"
            :transactions-file "transactions.csv"
            :target-currency :GBP
            :partner "HSBC"})

;(-main input)