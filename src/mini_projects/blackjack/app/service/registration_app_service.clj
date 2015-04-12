(ns mini-projects.blackjack.app.service.registration-app-service
  (:require [mini-projects.blackjack.util.shared :as s]
            [mini-projects.blackjack.config.registry :as r]
            [mini-projects.blackjack.port.game-repository :as gr]
            [mini-projects.blackjack.port.player-repository :as pr]
            [mini-projects.blackjack.app.cashier :as c]
            [mini-projects.blackjack.domain.game.game :as g]
            ))

(defn register! [player-name]
  (let [id (s/generate-id)
        player {:id id :name player-name :win-number 0}]
    (c/create-account! id)
    (pr/save-player! r/player-repository player)
    id))