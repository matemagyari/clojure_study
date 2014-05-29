(ns blackjack.domain.game.end-to-end-test
  (:require
            [blackjack.app.eventbus :as e]
            [blackjack.app.eventhandlers :as eh]
            [blackjack.util.shared :as s]
            [blackjack.config.registry :as r]
            [blackjack.app.service.game-app-service :as gs]
            [blackjack.app.service.seating-app-service :as ss]
            [blackjack.app.service.registration-app-service :as rs]
            [blackjack.domain.game.game-test :as gt]
            [blackjack.domain.game.game :as g]
            [blackjack.domain.game.game-repository :as gr]
            [blackjack.domain.player.player :as p]
            [blackjack.domain.player.player-repository :as pr]
            [blackjack.domain.table.table :as t]
            [blackjack.domain.table.table-repository :as tr]
            [blackjack.domain.cashier.wallet-service :as w]
            [clojure.test :as test]
            ))


;;cleanup
(e/reset-event-bus!)
(pr/clear! r/player-repository)
(tr/clear! r/table-repository)
(gr/clear! r/game-repository)
(w/clear! r/wallet-service)

(def table (t/create-new-table))
(tr/save-table! r/table-repository table)

(defn flush-events! []
  (e/flush-events-with! (eh/event-handlers)))

(test/deftest a-game-test
  (with-redefs [g/new-deck gt/prepared-deck-for-long-game]
    (let [table-id (:id table)
          player-name "Paul"
          dealer-name "Dean"
          player-id (rs/register! player-name)
          dealer-id (rs/register! dealer-name)]
      (ss/seat-player! dealer-id table-id)
      (ss/seat-player! player-id table-id)
      (flush-events!)
      (let [game (first (gr/get-games r/game-repository))
            game-id (:id game)
            player-hit-action {:game-id game-id :player-id player-id :type :hit}
            dealer-hit-action {:game-id game-id :player-id dealer-id :type :hit}]
        (println "player" player-id "dealer" dealer-id)
        (println "the game" game)
        (gs/handle-action! player-hit-action)
        ;(gs/handle-action! dealer-hit-action)
        ))

      ))

(test/run-tests)

(println "Yup Finished")