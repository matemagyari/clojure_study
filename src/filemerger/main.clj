(ns filemerger.main
  (:require [clojure.test :as test]
            [filemerger.infrastructure :as i]
            [filemerger.app :as app]))

;; ================ POOR MAN's DI CONTAINER ================================
(defn get-processor
  "Creates the processor function"
  []
  (app/create-transactions-processor i/read-transactions i/read-exchange-rates #(println (str "The result is " %))))

;; ================ MAIN ================================

(defn -main
  "The main function"
  [args]
  (let [process (get-processor)]
    (process args)))


;; ========================= TESTS ==============================

(defn is= [a b]
  (test/is (= a b)))

(def input {:exchange-rates-file "/Users/mate.magyari/IdeaProjects/ScalaStudy/exchangerates.csv"
            :transactions-file "/Users/mate.magyari/IdeaProjects/ScalaStudy/transactions.csv"
            :target-currency :gdp})

(test/deftest processor-tests
  (let [result (atom nil)
        process (app/create-transactions-processor
                  i/read-transactions
                  i/read-exchange-rates
                  #(reset! result %))]
    (process input)
    (is= {:amount -438.28039917608690465930415601938752M, :currency :gdp} @result)))

(test/run-tests)

;(-main input)