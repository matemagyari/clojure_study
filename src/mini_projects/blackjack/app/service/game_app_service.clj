(ns mini-projects.blackjack.app.service.game-app-service
  (:require [mini-projects.blackjack.util.shared :as s]
            [mini-projects.blackjack.config.registry :as r]
            [mini-projects.blackjack.port.game-repository :as gr]
            [mini-projects.blackjack.app.eventbus :as e]
            [mini-projects.blackjack.domain.game.game :as g]
            [mini-projects.blackjack.app.lockable :refer [with-lock]]))

(defn- action-on-game [action-type player game]
  (condp = action-type
    :hit (g/hit game player)
    :stand (g/stand game player)))

(defn handle-action! [action]
  "Handles player action"
  (with-lock (:game-id action) r/game-repository
    (let [game (->> (gr/get-game r/game-repository (:game-id action))
                 (action-on-game (:type action) (:player-id action)))
          [events,game] (s/remove-events game)]
      (gr/save-game! r/game-repository game)
      (e/publish-events! events))))
