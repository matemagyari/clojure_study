(ns mini-projects.blackjack.domain.game.game-test
  (:use clojure.test
        mini-projects.blackjack.domain.game.game))

(defn prepared-deck-for-long-game []
  [[:club :2]
   [:diamond :2]
   [:heart :2]
   [:spade :2]
   [:club :3]
   [:diamond :3]
   [:heart :3]
   [:spade :3]
   [:club :4]
   [:diamond :4]
   [:heart :4]
   [:spade :4]
   [:club :5]
   [:diamond :5]
   [:heart :5]   
   [:spade :5]   
   ])

;helper functions
(def table-id "123")
(def player "Paul")
(def dealer "Dean")

(defn- create-new-game []
  (new-game table-id dealer player (new-deck)))

(defn- is-player-won [player a-game]
  (is (= :finished (:state a-game)))
  (is (= player (winner-of a-game))))

(defn- is-game-finished [a-game]
  (is (= :finished (:state a-game))))

(deftest player-wins-with-blackjack
  (with-redefs [new-deck (fn [] [[:club :A]
                                 [:spade :K]
                                 [:diamond :10]
                                 [:heart :10]])]
    (let [a-game (-> (create-new-game)
                   (deal-initial-cards)
                   (stand player)
                   (stand dealer))]
      (is-player-won player a-game))))

(deftest dealer-wins-with-blackjack
  (with-redefs [new-deck (fn [] [[:club :K]
                                 [:spade :A]
                                 [:diamond :10]
                                 [:heart :J]])]
    (let [a-game (-> (create-new-game)
                   (deal-initial-cards)
                   (stand player)
                   (stand dealer))]
      (is-player-won dealer a-game))))


(deftest player-wins-with-18-against-17
  (with-redefs [new-deck (fn [] [[:club :2]
                                 [:spade :2]
                                 [:diamond :7]
                                 [:heart :J]
                                 [:diamond :5]
                                 [:heart :3]
                                 [:diamond :4]
                                 [:heart :2]])]
    (let [a-game (-> (create-new-game)
                   (deal-initial-cards)
                   (hit player)
                   (hit dealer)
                   (hit player)
                   (hit dealer)
                   (stand player)
                   (stand dealer))]
      (is-player-won player a-game))))

(deftest player-busts
  (with-redefs [new-deck (fn [] [[:club :10]
                                 [:spade :2]
                                 [:diamond :7]
                                 [:heart :J]
                                 [:diamond :5]])]
    (let [a-game (-> (create-new-game)
                   (deal-initial-cards)
                   (hit player))]
      (is-player-won dealer a-game))))


(deftest dealer-busts
  (with-redefs [new-deck (fn [] [[:club :2]
                                 [:spade :8]
                                 [:diamond :7]
                                 [:heart :J]
                                 [:diamond :3]
                                 [:diamond :4]])]
    (let [a-game (-> (create-new-game)
                   (deal-initial-cards)
                   (hit player)
                   (hit dealer))]
      (is-player-won player a-game))))

(deftest a-full-game-test
  (with-redefs [new-deck prepared-deck-for-long-game]
    (let [a-game (create-new-game)
          a-game (deal-initial-cards a-game)]
      
      (testing "dealer tries to make the first hit"
               (is (thrown? RuntimeException
                            (hit a-game dealer))))
    
      (testing "player tries to draw twice in a row"
               (let [a-game (hit a-game player)]
                 (is (thrown? RuntimeException
                              (hit a-game player))))) 
      
      (testing "player tries to draw and stand in a row"
               (let [a-game (hit a-game player)]
                 (is (thrown? RuntimeException
                              (stand a-game player)))))
      
      (testing "player tries to act after stand"
               (let [a-game (stand a-game player)
                     a-game (hit a-game dealer)]
                 (is (thrown? RuntimeException
                              (hit a-game player))))) 
      )))

(run-tests)
