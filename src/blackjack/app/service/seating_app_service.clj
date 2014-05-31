(ns blackjack.app.service.seating-app-service
  (:require [blackjack.util.shared :as s]
            [blackjack.config.registry :as r]
            [blackjack.domain.table.table :as t]
            [blackjack.app.eventbus :as e]
            [blackjack.domain.table.table-repository :as tr]))

(defn seat-player! [player-id table-id]
  (let [table (-> (tr/get-table r/table-repository table-id)
                (t/sit player-id))
        [events,t] (s/remove-events table)]
    (e/publish-events! events)
    (when (s/seq-contains? (:players t) player-id)
      (tr/save-table! r/table-repository t))))