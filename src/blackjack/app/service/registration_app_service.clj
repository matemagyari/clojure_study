(ns blackjack.app.service.registration-app-service
  (:require [blackjack.util.shared :as s]
            [blackjack.config.registry :as r]
            [blackjack.domain.game.game-repository :as gr]
            [blackjack.domain.player.player-repository :as pr]
            [blackjack.domain.game.game :as g]
            [blackjack.domain.cashier.cashier :as c]))

(defn register! [player-name]
  (let [id (s/generate-id)
        player {:id id :name player-name}]
    (c/create-account! id)
    (pr/save-player! r/player-repository player)
    id))