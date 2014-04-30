(ns blackjack.table-repository
    (:require [blackjack.shared :as shared]))

(def table-map (ref {}))

(defprotocol TableRepository
  (clear! [this])
  (get-table [this table-id])
  (get-tables [this])
  (save-table! [this table]))

(defrecord InMemoryTableRepository []
  TableRepository
  (clear! [this] (dosync 
                   (ref-set table-map {})))
  (get-table [this table-id] (get @table-map table-id))
  (get-tables [this] (vals @table-map))
  (save-table! [this table] 
    (dosync
      (alter table-map into {(table :id) table}))))

(def table-repository (InMemoryTableRepository.))




