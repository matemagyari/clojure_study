(ns bigdata-aggregator.app
  (:import [java.io File])
  (:require [clojure.test :as test]
            [clojure.java.io :as io]
            [bigdata-aggregator.domain :as d]
            [bigdata-aggregator.infrastructure :as i]))

(defn process-input
  "Processes the input and puts the results to the output"
  [{:keys [transactions-file exchange-rates-file target-currency partner]}]
  (let [exchange-rates (i/read-exchange-rates exchange-rates-file)
        line-seq->transaction-seq #(map i/line->transaction %)
        aggregate-transactions-by-partner #(d/aggregate-transactions-by-partner (line-seq->transaction-seq %) target-currency exchange-rates)
        aggregate-transactions-of-partner #(d/aggregate-transactions-of-partner (line-seq->transaction-seq %) partner target-currency exchange-rates)]

    {:result-by-partner (i/reduce-file-line-by-line transactions-file aggregate-transactions-by-partner)
     :result-of-partner (i/reduce-file-line-by-line transactions-file aggregate-transactions-of-partner)}))

;; ========================= TESTS ==============================

(defn is= [a b]
  (test/is (= a b)))


(test/deftest process-input-tests
  (let [num-of-transactions (* 10 10)
        num-of-partners 100
        num-of-currencies 100
        transaction-file-name "transactions.csv"
        exchange-rates-file-name "exchangerates.csv"
        currencies (let [letters (map char (range 65 91))] ;[A-Z]
                     (take num-of-currencies (for [c1 letters c2 letters c3 letters]
                                               (str c1 c2 c3))))
        partners (for [x (range num-of-partners)] (str "P" x))
        input {:exchange-rates-file exchange-rates-file-name
               :transactions-file transaction-file-name
               :target-currency (-> currencies first keyword)
               :partner (first partners)}
        delete-file! (fn [f] (-> f File. .delete))
        delete-files! (fn []
                        (delete-file! transaction-file-name)
                        (delete-file! exchange-rates-file-name))]
    (do
      (delete-files!)
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
      (println (process-input input))
      (println "Time: " (- (System/currentTimeMillis) start))
      (delete-files!))))

(test/run-tests
  'bigdata-aggregator.app
  'bigdata-aggregator.domain
  'bigdata-aggregator.infrastructure)





