(ns blackjack.domain.table.table-repository
    (:require [blackjack.util.shared :as shared]))

(defprotocol TableRepository
  (clear! [this])
  (get-table [this table-id])
  (get-tables [this])
  (save-table! [this table]))