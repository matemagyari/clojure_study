(ns mini-projects.blackjack.app.service.seating-app-service
  (:require [mini-projects.blackjack.util.shared :as s]
            [mini-projects.blackjack.config.registry :as r]
            [mini-projects.blackjack.domain.table.table :as t]
            [mini-projects.blackjack.app.eventbus :as e]
            [mini-projects.blackjack.port.table-repository :as tr]
            [mini-projects.blackjack.app.lockable :refer [with-lock]]))

(defn seat-player! [player-id table-id]
  (with-lock table-id r/table-repository
    (let [table (-> (tr/get-table r/table-repository table-id)
                  (t/sit player-id))
          [events,t] (s/remove-events table)]
      (when (s/seq-contains? (:players t) player-id)
        (tr/save-table! r/table-repository t))
      (e/publish-events! events))))