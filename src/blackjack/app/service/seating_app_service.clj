(ns blackjack.app.service.seating-app-service
  (:require [blackjack.util.shared :as s]
            [blackjack.config.registry :as r]
            [blackjack.domain.table.table :as t]
            [blackjack.domain.table.table-repository :as tr]))

(defn seat-player! [{player-id :player-id table-id :table-id}]
  (let [table (-> (tr/get-table r/table-repository table-id)
                (t/sit player-id))]
    (when (s/seq-contains? (:players table) player-id)
      (tr/save-table! r/table-repository table))))