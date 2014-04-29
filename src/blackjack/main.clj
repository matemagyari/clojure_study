(ns blackjack.main
  (:require [blackjack.events :as e]
            [blackjack.eventhandlers :as eh]
            [blackjack.shared :as shared]
            [blackjack.game-repository :as gr]
            [blackjack.game :as g]
            [blackjack.table :as t]
            [blackjack.player :as p]))

(e/reset-event-bus!)
(gr/clear! gr/game-repository)

(def p1 :Joe)
(def p2 :Jane)
(def t (t/create-new-table))
(def t1 (-> t
          (t/sit p1)
          (t/sit p2)))

(e/flush-events-with! (eh/event-handlers)) 

;;(println gr/game-repository)

;;(def the-game (gr/get-game game-repo))