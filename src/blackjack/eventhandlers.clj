(ns blackjack.eventhandlers
  (:require [blackjack.events :as events]
            [blackjack.shared :as shared]
            [blackjack.game-repository :as gr]
            [blackjack.table-repository :as tr]
            [blackjack.player-repository :as pr]
            [blackjack.player :as p]
            [blackjack.table :as t]
            [blackjack.game :as g]))

(defn- create-handler [[& types-to-match] do-fn]
    { :match-fn (fn [event] (shared/seq-contains? types-to-match (event :type)))
      :do-fn do-fn })

(defn- player-card-dealt-handler []
  (create-handler [:player-card-dealt-event]
                  (fn [event] (println "CardsDealt! " event))))

(defn- table-is-full-handler []
  (create-handler [:table-is-full-event] 
                  (fn [event] (let [players (event :players)
                                    dealer (first players)
                                    player (last players)
                                    game (g/new-game (event :table-id) dealer player)
                                    started-game (g/deal-initial-cards game)]
                                (gr/save-game! gr/game-repository started-game)))))

(defn- game-finished-event-table-clear-handler [] 
  (create-handler [:game-finished-event]
                  (fn [event] (let [repo (tr/get-table-repository)
                                    table (tr/get-table repo (event :table-id))
                                    updated-table (t/clear-table table)]
                                (tr/save-table repo updated-table)))))

(defn- game-finished-event-player-update-handler [] 
  (create-handler [:game-finished-event]
                  (fn [event] (let [repo (pr/get-player-repository)
                                    player (pr/get-player repo (event :player-id))
                                    updated-player (p/record-win player)]
                                (pr/save-player repo updated-player)))))

(defn- game-started-event-handler [] 
  (create-handler [:game-started-event]
                  (fn [event] (println "Game started event! " event))))

(defn- game-event-handler [] 
  (create-handler [:game-started-event :game-finished-event]
                  (fn [event] (println "Game event! " event))))

(defn event-handlers []
  "Returns a list of event handlers"
  [ (table-is-full-handler)
    (game-started-event-handler)
    (game-event-handler)
    (game-finished-event-table-clear-handler)
    (game-finished-event-player-update-handler)
    (player-card-dealt-handler)])

