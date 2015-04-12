(ns mini-projects.blackjack.main
  (:require [mini-projects.blackjack.app.eventbus :as e]
            [mini-projects.blackjack.app.eventhandlers :as eh]
            [mini-projects.blackjack.util.shared :as shared]
            [mini-projects.blackjack.config.registry :as r]
            [mini-projects.blackjack.port.player-repository :as pr]
            [mini-projects.blackjack.domain.player.player :as p]
            [mini-projects.blackjack.domain.game.game :as g]
            [mini-projects.blackjack.port.game-repository :as gr]
            [mini-projects.blackjack.domain.table.table :as t]
            [mini-projects.blackjack.port.table-repository :as tr]
            ;;[clojure.tools.trace :as trace]
            ))

(println "here I am")
