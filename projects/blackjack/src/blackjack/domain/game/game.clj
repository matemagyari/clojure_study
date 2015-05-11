(ns in.game.game
  (:require [mini-projects..shared :as s]ojure.tools.trace)
;;(clojure.tools.trace/trace-ns mini-projects.blackjack.domaace-ns mini-projects.blackjack.domain.game)

(defn- is-player-id? [player-id]
  (string? player-id))

(defn player-with-role [game role]
  {:pre [ (is-player-id? (first (keys (game :players)))) ]
   :post [(is-player-id? %)]}
  (let [filter-fn (fn [[key val]]
                    (= (get-in val [:role]) role))
        [[key val]] (filter filter-fn (game :players))]
    key))


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

(defn new-deck []
  {:post [(= (count %) 52)]}
  "Creates a new shuffled deck"
  (shuffle 
    (for [suite suites
          rank ranks]
      [suite rank])))

(defn- draw [deck]
  {:pre [(not-empty deck)]}
  "Draws the top card, returning the card and the remaining deck"
  [(first deck) (rest deck)])

(defn new-game [table-id dealer-id player-id deck]
  {:pre [ (is-player-id? dealer-id) (is-player-id? player-id) ]}
  "Creates a new Game structure"
  { :table-id table-id
    :players { player-id { :cards #{} 
                           :role :player}
               dealer-id { :cards #{}
                           :role :dealer}}
    :state :initialized
    :id (s/generate-id)
    :deck deck})

(defn- card-value [card]
  {:post [(number? %)]}
  "Value of a card"
  ((last card) rank-values))

(defn- score-hand [cards]
  {:pre [(< 0 (count cards))]
   :post [(number? %)]}
  "Calculates the score for the hand"
  (let [sum (reduce + (map card-value cards))
        ace? #(= :A (last %))
        ace-count (count (filter ace? cards))]
    (if (> ace-count 1) 
      (- sum 10)
      sum)))

(defn score [game player]
  (score-hand (get-in game [:players player :cards])))

(defn- finish-game [game winner]
  (-> game
    (s/add-event {:game-id (:id game) :table-id (:table-id game) :winner winner :type :game-finished-event})
    (assoc :state :finished)))

(defn other-player [game player]
  {:pre [ (is-player-id? player) ]
   :post [ (is-player-id? %) (not= player %)] }
  (let [the-player (player-with-role game :player)
        the-dealer (player-with-role game :dealer)]
    (if (= the-player player) the-dealer the-player )))

(defn- hit-after [game player]
  "Do things after player hits"
  (if (> (score game player) target)
    (finish-game game (other-player game player))
    game))

(defn- check-not-out-of-turn [game player]
  (when (= player (:last-to-act game)) 
    (s/raise-domain-exception (str "Player " player " acts out of turn in game " (:id game)))))

(defn- check-player-not-stand [game player]
  (when (= :stand (get-in game [:players player :state])) 
    (s/raise-domain-exception (str "Player " player " stands in game " (:id game)))))

(defn- check-game-state [game state]
  (when-not (= state (get game :state)) 
    (s/raise-domain-exception (str "Game " (:id game) " is not in state " state))))

(defn- check-player-can-act [game player]
  (check-not-out-of-turn game player)
  (check-player-not-stand game player)
  (check-game-state game :started))

(defn hit [game player]
  {:pre [ (is-player-id? player) (s/not-nil? game) ]}
  "Player hits"
  (check-player-can-act game player)
  (let [[card deck] (draw (game :deck))]
    (-> game
        (s/add-event {:game-id (:id game) :table-id (:table-id game)
                      :player-id player :card card :type :player-card-dealt-event})
        (update-in [:players player :cards] #(cons card %))
        (assoc :deck deck)
        (assoc :last-to-act player)
        (hit-after player))))

(defn deal-initial-cards [game]
  {:pre [(= :initialized (:state game))]
   :post [(= :started (:state %))]}
  "Deals initial cards"
  (check-game-state game :initialized)
  (let [dealer (player-with-role game :dealer)
        player (player-with-role game :player)]
    (-> game
      (s/add-event {:game-id (:id game) :table-id (:table-id game) :type :game-started-event})
      (assoc :state :started)
      (hit player)
      (hit dealer)
      (hit player)
      (hit dealer))))

(defn winner-of [game]
  {:post [(is-player-id? %)]}
  (let [player (player-with-role game :player)
        dealer (player-with-role game :dealer)
        player-score (score game player)
        dealer-score (score game dealer)]
    (cond 
      (> player-score target) dealer
      (> dealer-score target ) player
      (> (s/abs player-score) (s/abs dealer-score)) player
      :else dealer)))

(defn stand [game player]
  {:pre [ (is-player-id? player) ]}
  "Player stands"
  (check-player-can-act game player)
  (let [updated-game (-> game
                       (s/add-event {:game-id (:id game) :player-id player :table-id (:table-id game) :type :player-stands-event})
                       (assoc-in [:players player :state] :stand)
                       (assoc :last-to-act player))
        other (other-player game player)
        both-stands (= :stand (get-in game [:players other :state]))]
    (if-not both-stands
      updated-game
      (finish-game game (winner-of game)))))

