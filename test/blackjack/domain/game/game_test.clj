(ns blackjack.domain.game.game-test
  (:use clojure.test
        blackjack.domain.game.game))
 
(defn prepared-deck []
  [[:heart :jack]
   [:diamond :king]
   ])

(deftest a-full-game-test
  (with-redefs [new-deck prepared-deck]
  (let [dealer "p1"
        player "p2"
        table-id "123"
        a-game (new-game table-id dealer player)
        a-game (deal-initial-cards a-game)]
    
    (testing "dealer tries to make the first hit"
             (is (thrown? RuntimeException
                          (hit a-game dealer))))
    
    (testing "player tries to draw twice in a row"
             (let [a-game (hit a-game player)]
               (is (thrown? RuntimeException
                            (hit a-game player)))))))
)
(run-tests)