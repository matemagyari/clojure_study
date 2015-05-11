(ns blackjack.port.table-repository
    (:require [mini-projects.blackjack.util.shared :as shared]))

(defprotocol TableRepository
  (clear! [this])
  (get-table [this table-id])
  (get-tables [this])
  (save-table! [this table]))