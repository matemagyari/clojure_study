(ns blackjack.app.service.game-app-service
  (:require [blackjack.util.shared :as shared]
            [blackjack.config.registry :as r]
            [blackjack.domain.game.game-repository :as gr]
            [blackjack.domain.game.game :as g]))

(defn- action-on-game [action-type player game]
  (condp = action-type
    :hit (g/hit game player)
    :stand (g/stand game player)))

(defn handle-action! [action]
  "Handles player action"
  (->> (gr/get-game r/game-repository (:game-id action))
       (action-on-game (:type action) (:player-id action))
       (gr/save-game! r/game-repository)))
