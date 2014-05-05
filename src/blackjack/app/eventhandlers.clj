(ns blackjack.app.eventhandlers
  (:require [blackjack.app.eventbus :as events]
            [blackjack.util.shared :as shared]
            [blackjack.config.registry :as r]
            [blackjack.domain.player.player-repository :as pr]
            [blackjack.domain.player.player :as p]
            [blackjack.domain.table.table-repository :as tr]
            [blackjack.domain.table.table :as t]
            [blackjack.domain.game.game-repository :as gr]
            [blackjack.domain.game.game :as g]))

(defn- create-handler [[& types-to-match] do-fn]
    { :match-fn (fn [event] (shared/seq-contains? types-to-match (event :type)))
      :do-fn do-fn })

(def external-bus-sender
  (fn [event] (events/publish! r/external-event-bus event)))

(defn- player-card-dealt-handler []
  (create-handler [:player-card-dealt-event]
                  external-bus-sender))

(defn- table-is-full-handler []
  (create-handler [:table-is-full-event] 
                  (fn [event] (let [players (event :players)
                                    dealer (first players)
                                    player (last players)
                                    game (g/new-game (event :table-id) dealer player)
                                    started-game (g/deal-initial-cards game)]
                                (gr/save-game! r/game-repository started-game)))))

(defn- game-finished-event-table-clear-handler [] 
  (create-handler [:game-finished-event]
                  (fn [event] (let [repo r/table-repository
                                    table (tr/get-table repo (event :table-id))
                                    updated-table (t/clear-table table)]
                                (tr/save-table! repo updated-table)))))

(defn- game-finished-event-player-update-handler [] 
  (create-handler [:game-finished-event]
                  (fn [event] (let [repo r/player-repository
                                    player (pr/get-player repo (event :winner))
                                    updated-player (p/record-win player)]
                                (pr/save-player! repo updated-player)))))

(defn- game-started-event-handler [] 
  (create-handler [:game-started-event]
                  external-bus-sender))

(defn- game-event-handler [] 
  (create-handler [:game-started-event :game-finished-event]
                  external-bus-sender))

(defn event-handlers []
  "Returns a list of event handlers"
  [ (table-is-full-handler)
    (game-started-event-handler)
    (game-event-handler)
    (game-finished-event-table-clear-handler)
    (game-finished-event-player-update-handler)
    (player-card-dealt-handler)])

