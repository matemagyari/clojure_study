(ns blackjack.game
  (:require [blackjack.events :as events]
            [blackjack.shared :as shared]
            [clojure.test :as t]
            [clojure.tools.trace :as tr]))

;;(use 'clojure.tools.trace)
;;(clojure.tools.trace/trace-ns blackjack.game)
;;(trace-ns blackjack.game)

(defn player-with-role [game role]
  (let [filter-fn (fn [entry]
                    (= (get-in (second entry) [:role]) role))
        entry (filter filter-fn (game :players))]
    (first entry)))


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

(defn new-game [table-id dealer-id player-id]
  "Creates a new Game structure"
  { :table-id table-id
    :players { player-id { :cards #{} 
                           :role :player}
               dealer-id { :cards #{}
                           :role :dealer}}
    :state :initialized
    :id (shared/generate-id)
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
  (events/publish-event {:game-id (:id game) :winner winner :type :game-finished-event})
  (assoc game :state :finished))

(defn- other-player [game player]
  (if (= (player-with-role game :player) player)
    (player-with-role game :dealer)
    player))

(defn- hit-after [game player]
  "Do things after player hits"
  (if (> (score game player) target)
    (finish-game game (other-player game player))
    game))

(defn- check-not-out-of-turn [game player]
  (when (= (player :id) (:last-to-act game)) 
    (shared/raise-domain-exception (str "Player " player " acts out of turn in game " (:id game)))))

(defn- check-player-not-stand [game player]
  (when (= :stand (get-in game [:players player :state])) 
    (shared/raise-domain-exception (str "Player " player " stands in game " (:id game)))))

(defn- check-game-state [game state]
  (when-not (= state (get game :state)) 
    (shared/raise-domain-exception (str "Game " (:id game) " is not in state " state))))

(defn- check-player-can-act [game player]
  (check-not-out-of-turn game player)
  (check-player-not-stand game player)
  (check-game-state game :started))

(defn hit [game player]
  "Player hits"
  (check-player-can-act game player)
  (let [[card deck] (draw (game :deck))]
    (events/publish-event {:game-id (:id game) :player player :card card :type :player-card-dealt-event})
    (-> game
        (update-in [:players player :cards] #(cons card %))
        (assoc :deck deck)
        (assoc :last-to-act (player :id))
        (hit-after player))))

(defn deal-initial-cards [game]
  "Deals initial cards"
  (check-game-state game :initialized)
  (events/publish-event {:game-id (:id game) :type :game-started-event})
  (let [dealer (get game :dealer)
        player (get game :player)]
    (-> game
      (assoc :state :started)
      (hit player)
      (hit dealer)
      (hit player)
      (hit dealer))))

(defn winner-of [game]
  (let [player (game :player)
        dealer (game :dealer)
        get-score (fn [p] (score game p)) ;;a closure
        get-diff-from-target (fn [p] (- (get-score p) target))
        player-diff-from-target (get-diff-from-target player)]
    (if (or (pos? player-diff-from-target)
             (> player-diff-from-target (get-diff-from-target dealer)))
             dealer
             player)))

(defn stand [game player]
  "Player stands"
  (check-player-can-act game player)
  (events/publish-event {:game-id (:id game) :player player :type :player-stands-event})
  (let [updated-game (-> game
                       (assoc-in [:players player :state] :stand)
                       (assoc :last-to-act (player :id)))
        both-stands (= :stand ((other-player game player) :state))]
    (println "BOOOOTH" both-stands player (other-player game player))
    (if-not both-stands
      updated-game
      (finish-game game (winner-of game)))))


;;===================== TESTS ==========================
;(def g (new-game 1 :john :jane))
;(defn run-test-game []
;  (-> g
;          (deal-initial-cards)
;          (stand :jane)
;          (stand :john)))

;(def g2 (-> g
;          (deal-initial-cards)
;          (hit :jane)
;          (hit :john)
;          (hit :jane)
;          (hit :john)))

;(deftest hit-test)

;(run-tests 'blackjack.game)