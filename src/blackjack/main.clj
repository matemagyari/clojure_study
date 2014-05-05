(ns blackjack.main
  (:require [blackjack.app.eventbus :as e]
            [blackjack.app.eventhandlers :as eh]
            [blackjack.util.shared :as shared]
            [blackjack.config.registry :as r]
            [blackjack.domain.player.player-repository :as pr]
            [blackjack.domain.player.player :as p]
            [blackjack.domain.game.game :as g]
            [blackjack.domain.game.game-repository :as gr]
            [blackjack.domain.table.table :as t]
            [blackjack.domain.table.table-repository :as tr]
            [clojure.tools.trace :as trace]))

(e/reset-event-bus!)
(gr/clear! r/game-repository)
(pr/clear! r/player-repository)
(tr/clear! r/table-repository)

(def player1 (p/create-player "Joe"))
(def player2 (p/create-player "Jane"))

(def p1 (player1 :id))
(def p2 (player2 :id))

(pr/save-player! r/player-repository player1)
(pr/save-player! r/player-repository player2)

(def t (t/create-new-table))
(tr/save-table! r/table-repository t)

(def t1 (-> t
          (t/sit p1)
          (t/sit p2)))

(tr/save-table! r/table-repository t)

(e/flush-events-with! (eh/event-handlers)) 

(def game (first (gr/get-games r/game-repository)))

(def game1 (-> game
             (g/stand p1)
             (g/stand p2)))

(e/flush-events-with! (eh/event-handlers)) 

;;(g/player-with-role game :dealer)

;;(println gr/game-repository)

;;(def the-game (gr/get-game game-repo))