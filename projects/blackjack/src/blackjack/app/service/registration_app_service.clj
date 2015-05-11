(ns blackjack.app.service.registration-app-service
  (:require [blackjack.util.shared :as s]
            [blackjack.config.registry :as r]
            [blackjack.port.game-repository :as gr]
            [blackjack.port.player-repository :as pr]
            [blackjack.app.cashier :as c]
            [blackjack.domain.game.game :as g]
            ))

(defn register! [player-name]
  (let [id (s/generate-id)
        player {:id id :name player-name :win-number 0}]
    (c/create-account! id)
    (pr/save-player! r/player-repository player)
    id))