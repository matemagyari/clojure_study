(ns blackjack.app.eventhandlers
  (:require [blackjack.app.eventbus :as e]
            [blackjack.util.shared :as s]
            [blackjack.config.registry :as r]
            [blackjack.app.cashier :as c]
            [blackjack.port.player-repository :as pr]
            [blackjack.domain.player.player :as p]
            [blackjack.port.table-repository :as tr]
            [blackjack.domain.table.table :as t]
            [blackjack.port.game-repository :as gr]
            [blackjack.domain.game.game :as g]
            [blackjack.port.external-event-publisher :as eep]
            [clojure.contrib.core :only [dissoc-in] :as ccc]))

(defn create-handler [[& types-to-match] do-fn]
  {:types types-to-match
   :match-fn (fn [event] (s/seq-contains? types-to-match (event :type)))
   :do-fn do-fn})

(def external-bus-sender
  (fn [event] (eep/publish! r/external-event-bus event)))

(defn external-bus-public-sender! [event]
  (eep/publish! r/external-event-bus (eep/to-public-external-event event)))

(defn player-card-dealt-handler []
  (create-handler [:player-card-dealt-event]
    (fn [event]
      (let [private-event (-> event
                            eep/to-private-external-event
                            (assoc-in [:event :type] :private-card-dealt-event))
            public-event (-> event
                           eep/to-public-external-event
                           (assoc-in [:event :type] :public-card-dealt-event)
                           (ccc/dissoc-in [:event :card]))]
        (do
          (eep/publish! r/external-event-bus private-event)
          (eep/publish! r/external-event-bus public-event))))))

(defn table-is-full-handler []
  (create-handler [:table-is-full-event]
    (fn [event] (let [players (event :players)
                      dealer (first players)
                      player (last players)
                      deck (g/new-deck)
                      game (g/new-game (event :table-id) dealer player deck)
                      game (g/deal-initial-cards game)
                      [events,game] (s/remove-events game)]
                  (c/debit-entry-fee! dealer)
                  (c/debit-entry-fee! player)
                  (gr/save-game! r/game-repository game)
                  (e/publish-events! events)))))

(defn game-finished-event-table-clear-handler []
  (create-handler [:game-finished-event]
    (fn [event] (let [repo r/table-repository
                      table (tr/get-table repo (event :table-id))
                      updated-table (t/clear-table table)]
                  (tr/save-table! repo updated-table)))))

(defn game-finished-event-balance-update-handler []
  (create-handler [:game-finished-event]
    (fn [event] (c/give-win! (:winner event)))))

(defn game-finished-event-player-update-handler []
  (create-handler [:game-finished-event]
    (fn [event] (let [repo r/player-repository
                      player (pr/get-player repo (event :winner))
                      updated-player (p/record-win player)]
                  (pr/save-player! repo updated-player)))))

(defn game-event-handler []
  (create-handler [:game-started-event :game-finished-event :player-stands-event] external-bus-public-sender!))

(defn table-event-handler []
  (create-handler [:table-seating-changed-event] external-bus-public-sender!))

(defn event-handlers []
  "Returns a list of event handlers"
  [(table-is-full-handler)
   (table-event-handler)
   (game-event-handler)
   (game-finished-event-table-clear-handler)
   (game-finished-event-player-update-handler)
   (game-finished-event-balance-update-handler)
   (player-card-dealt-handler)])

