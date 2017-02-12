(ns economy.trading-tests
  (:require [clojure.test :refer :all]
            [economy.actor :refer :all]
            [economy.trading :refer :all]))

(deftest do-transaction-tests
  (let [commodity :food
        buyer (assoc (create-actor 1 :gas)
                :actor/commodities {commodity 0}
                :actor/money 10)
        seller (assoc (create-actor 2 commodity)
                 :actor/commodities {commodity 3}
                 :actor/money 2)
        {:keys [buyer
                seller]} (do-transaction
                           {:buyer     buyer
                            :seller    seller
                            :commodity commodity
                            :price     6})]
    (is (= 4 (:actor/money buyer)))
    (is (= 8 (:actor/money seller)))
    (is (= 1 (get-in buyer [:actor/commodities commodity])))
    (is (= 2 (get-in seller [:actor/commodities commodity])))))

(run-tests 'economy.trading-tests)
