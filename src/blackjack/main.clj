(ns blackjack.main
  (:require [blackjack.events :as e]
            [blackjack.eventhandlers :as eh]
            [blackjack.shared :as shared]
            [blackjack.game-repository :as gr]
            [blackjack.table-repository :as tr]
            [blackjack.player-repository :as pr]
            [blackjack.game :as g]
            [blackjack.table :as t]
            [blackjack.player :as p]
            [clojure.tools.trace :as trace]))

(e/reset-event-bus!)
(gr/clear! gr/game-repository)
(pr/clear! pr/player-repository)
(tr/clear! tr/table-repository)

(def player1 (p/create-player "Joe"))
(def player2 (p/create-player "Jane"))

(def p1 (player1 :id))
(def p2 (player2 :id))

(pr/save-player! pr/player-repository player1)
(pr/save-player! pr/player-repository player2)

(def t (t/create-new-table))
(def t1 (-> t
          (t/sit p1)
          (t/sit p2)))

(e/flush-events-with! (eh/event-handlers)) 

(def game (first (gr/get-games gr/game-repository)))

(def game1 (-> game
             (g/stand p1)
             (g/stand p2)))

;(e/flush-events-with! (eh/event-handlers)) 

;;(g/player-with-role game :dealer)

;;(println gr/game-repository)

;;(def the-game (gr/get-game game-repo))