(ns filemerger.main
  (:require [clojure.test :as test]
            [filemerger.infrastructure :as i]
            [filemerger.app :as app]))

;; ================ POOR MAN's DI CONTAINER ================================

;; ================ MAIN ================================

(defn -main
  "The main function"
  [args]
  (i/process-input args))


;; ========================= TESTS ==============================

(defn is= [a b]
  (test/is (= a b)))

(def input {:exchange-rates-file "/Users/mate.magyari/IdeaProjects/ScalaStudy/exchangerates.csv"
            :transactions-file "/Users/mate.magyari/IdeaProjects/ScalaStudy/transactions.csv"
            :target-currency :GBP
            :partner "Etwas"})

(test/deftest main-tests
  (-main input))

(test/run-tests)

;(-main input)