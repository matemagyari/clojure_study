(ns economy.trading-tests
  (:require [clojure.test :refer :all]
            [economy.actor :refer :all]
            [economy.trading :refer :all]))

(deftest do-transaction-tests
  (let [commodity :food
        buyer (assoc (create-actor 1 :gas)
                :commodities {commodity 0}
                :money 10)
        seller (assoc (create-actor 2 commodity)
                 :commodities {commodity 3}
                 :money 2)
        {:keys [buyer
                seller]} (do-transaction
                           {:buyer     buyer
                            :seller    seller
                            :commodity commodity
                            :price     6})]
    (is (= 4 (:money buyer)))
    (is (= 8 (:money seller)))
    (is (= 1 (get-in buyer [:commodities commodity])))
    (is (= 2 (get-in seller [:commodities commodity])))))

(run-tests 'economy.trading-tests)
