(ns blackjack.main
  (:require [blackjack.app.eventbus :as e]
            [blackjack.app.eventhandlers :as eh]
            [blackjack.util.shared :as shared]
            [blackjack.config.registry :as r]
            [blackjack.port.player-repository :as pr]
            [blackjack.domain.player.player :as p]
            [blackjack.domain.game.game :as g]
            [blackjack.port.game-repository :as gr]
            [blackjack.domain.table.table :as t]
            [blackjack.port.table-repository :as tr]
            ;;[clojure.tools.trace :as trace]
            ))

(println "here I am")
