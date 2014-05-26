(ns blackjack.domain.game.end-to-end-test
  (:require [blackjack.util.shared :as s]
            [blackjack.config.registry :as r]
            [blackjack.app.service.game-app-service :as gs]
            [blackjack.app.service.seating-app-service :as ss]
            [blackjack.app.service.registration-app-service :as rs]
            [blackjack.domain.game.game-test :as gt]
            [blackjack.domain.game.game :as g]
            [blackjack.domain.table.table :as t]
            [blackjack.domain.table.table-repository :as tr]
    :use clojure.test
        ))

(def table (t/create-new-table))
(tr/save-table! r/table-repository table)

(deftest a-game-test
  (with-redefs [g/new-deck gt/prepared-deck-for-long-game]
    (let [table-id (:table-id table)
          player-name "Paul"
          dealer-name "Dean"
          player-id (rs/register! player-name)
          dealer-id (rs/register! dealer-name)]
      (ss/seat-player! {:player-id dealer-id :table-id table-id})
      (ss/seat-player! {:player-id player-id :table-id table-id})
      

      )))

(run-tests)