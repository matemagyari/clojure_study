(ns blackjack.infrastructure.adapter.driving.tablerepository.in-memory
  (:use [blackjack.domain.table.table-repository]))

(def table-map (ref {}))

(defrecord InMemoryTableRepository []
  TableRepository
  (clear! [this] (dosync 
                   (ref-set table-map {})))
  (get-table [this table-id] (get @table-map table-id))
  (get-tables [this] (vals @table-map))
  (save-table! [this table] 
    (dosync
      (alter table-map into {(table :id) table}))))