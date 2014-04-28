(ns blackjack.eventhandlers
  (:require [blackjack.events :as events]
            [blackjack.shared :as shared]
            [blackjack.game-repository :as gr]
            [blackjack.game :as g]))

(defn- create-handler [[& types-to-match] do-fn]
    { :match-fn (fn [event] (shared/seq-contains? types-to-match (event :type)))
      :do-fn do-fn })

(defn- match-fn [type]
  (fn [event] (= type (event :type))))

(defn- player-card-dealt-handler []
  (create-handler [:player-card-dealt-event]
                  (fn [event] (println (str "CardsDealt! " event)))))

(defn- table-is-full-handler []
  (create-handler [:table-is-full-event] table-is-full-handler-fn))

(defn- table-is-full-handler-fn [event]
  (println (str "Table is full! " event))
  (let [repo (gr/get-game-repository)
        players (event :players)
        dealer (first players)
        player (last players)
        game (g/new-game (event :table-id) dealer player)]
    (gr/save-game repo game)))

(defn- game-finished-event-handler [] 
  (create-handler [:game-finished-event]
                  (fn [event] (println (str "Game finished! " event)))))

(defn- game-started-event-handler [] 
  (create-handler [:game-started-event]
                  (fn [event] (println (str "Game started! " event)))))

(defn- game-event-handler [] 
  (create-handler [:game-started-event :game-finished-event]
                  (fn [event] (println (str "Game event! " event)))))

(defn event-handlers []
  "Returns a list of event handlers"
  [ (table-is-full-handler)
    (game-started-event-handler)
    (game-event-handler)
    (game-finished-event-handler)
    (player-card-dealt-handler)])

