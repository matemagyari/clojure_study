(ns mini-projects.bigdata-aggregator.main
  (:import [java.io File])
  (:require [clojure.test :as test]
            [clojure.java.io :as io]
            [mini-projects.bigdata-aggregator.infrastructure :as i]
            [mini-projects.bigdata-aggregator.app :as app]))

;; ================ POOR MAN's DI CONTAINER ================================

;; ================ MAIN ================================

(defn -main
  "The main function"
  [args]
  (i/process-input args))


;; ========================= TESTS ==============================

(defn is= [a b]
  (test/is (= a b)))

(test/deftest main-tests
  (let [num-of-transactions (* 1000 1000)
        transaction-file-name "transactions.csv"
        exchange-rates-file-name "exchangerates.csv"
        currencies ["GBP" "USD" "HUF" "CHF"]
        partners ["Etwas" "Gamesys" "HSBC"]
        input {:exchange-rates-file exchange-rates-file-name
               :transactions-file transaction-file-name
               :target-currency (-> currencies first keyword)
               :partner (first partners)}
        delete-file (fn [f] (-> f File. .delete))]
    (do
      (delete-file transaction-file-name)
      (delete-file exchange-rates-file-name)
      (println "Creating files")
      (with-open [w (io/writer transaction-file-name)]
        (dotimes [i num-of-transactions]
          (let [partner (rand-nth partners)
                currency (rand-nth currencies)
                amount (rand 100)]
            (.write w (str partner "," currency "," amount "\n")))))
      (with-open [w (io/writer exchange-rates-file-name)]
        (doseq [from currencies
                to currencies :when (not= from to)
                :let [rate (rand 100)]]
          (.write w (str from "," to "," rate "\n"))))
      (def start (System/currentTimeMillis))
      (-main input)
      (println "Time: " (- (System/currentTimeMillis) start)))))

(test/run-tests)

;(-main input)