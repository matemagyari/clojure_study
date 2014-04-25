(ns blackjack.game
  (:use clojure-study.assertion)
  (:use clojure.test)
  (:use blackjack.events)
  ;(:use clojure.tools.trace)
  )

(def target 21)
(def suites [:club :heart :spade :diamond])
(def rank-values (array-map 
                  :2 2
                  :3 3
                  :4 4
                  :5 5
                  :6 6
                  :7 7
                  :8 8
                  :9 9
                  :10 10
                  :J 10
                  :Q 10
                  :K 10
                  :A 11))
(def ranks (keys rank-values))

(defn- new-deck []
  "Creates a new shuffled deck"
  (shuffle 
    (for [suite suites
          rank ranks]
      [suite rank])))

(defn- draw [deck]
  "Draws the top card, returning the card and the remaining deck"
  [(first deck) (rest deck)])

(defn- new-id []
  (rand-int 1000))

(defn new-game [dealer player]
  "Creates a new Game structure"
  { :players { player { :cards #{} }
               dealer { :cards #{} 
                        :role :dealer}}
    :player player
    :dealer dealer
    :state :initialized
    :id (new-id)
    :deck (new-deck)})

(defn- score-hand [cards]
  "Calculates the score for the hand"
  (let [value-fn (fn [card] ((last card) rank-values))
        sum (reduce + (map value-fn cards))
        ace? #(= :A (last %))
        ace-count (count (filter ace? cards))]
    (if (> ace-count 1) 
      (- sum 10)
      sum)))

(defn- score [game player]
  (score-hand (get-in game [:players player :cards])))

(defn- finish-game [game winner]
  (publish-event {:game-id (:id game) :winner winner :type :game-finished-event})
  (assoc game :state :finished))

(defn- other-player [game player]
  (if (= (get game :player) player)
    (get game :dealer)
    (get game :player)))

(defn- hit-after [game player]
  "Do things after player hits"
  (if (> (score game player) target)
    (finish-game game (other-player game player))
    game))

(defn- check-not-out-of-turn [game player]
  (when (= player (:last-to-act game)) 
    (throw (RuntimeException. (str "Player " player " acts out of turn in game " (:id game))))))

(defn- check-player-not-stand [game player]
  (when (= :stand (get-in game [:players player :state])) 
    (throw (RuntimeException. (str "Player " player " stands in game " (:id game))))))

(defn- check-game-state [game state]
  (when-not (= state (get game :state)) 
    (throw (RuntimeException. (str "Game " (:id game) " is not in state " state)))))

(defn- check-player-can-act [game player]
  (check-not-out-of-turn game player)
  (check-player-not-stand game player)
  (check-game-state game :started))

(defn hit [game player]
  "Player hits"
  (check-player-can-act game player)
  (let [[card deck] (draw (game :deck))]
    (publish-event {:game-id (:id game) :player player :card card :type :player-card-dealt-event})
    (-> game
        (update-in [:players player :cards] #(cons card %))
        (assoc :deck deck)
        (assoc :last-to-act player)
        (hit-after player))))

(defn deal-initial-cards [game]
  "Deals initial cards"
  (check-game-state game :initialized)
  (publish-event {:game-id (:id game) :type :game-started-event})
  (let [dealer (get game :dealer)
        player (get game :player)]
    (-> game
      (assoc :state :started)
      (hit player)
      (hit dealer)
      (hit player)
      (hit dealer))))


(defn stand [game player]
  "Player stands"
  (check-player-can-act game player)
  (publish-event {:game-id (:id game) :player player :type :player-stands-event})
  (-> game
    (assoc-in [:players player :state] :stand)
    (assoc :last-to-act player)))

(defn get-winner [game]
  (let [player-score (score (game :player))
        dealer-score (score (game :dealer))]
    (if (> player-score (score (game :dealer))))))

;;===================== TESTS ==========================
(def g (new-game 1 2))
(def g2 (-> g
          (deal-initial-cards)
          (hit 2)
          (hit 1)
          (hit 2)
          (hit 1)))


(deftest hit-test)

(run-tests 'blackjack.game)