(ns mini-projects.blackjack.infrastructure.adapter.driving.tablerepository.in-memory
  (:require [mini-projects.blackjack.port.table-repository :refer :all]
            [mini-projects.blackjack.app.lockable :refer :all]
            [mini-projects.blackjack.infrastructure.adapter.driving.shared.locking :as lo]))

(def ^:private table-map (ref {}))
(def ^:private locks (ref {}))

(defrecord InMemoryTableRepository []
  TableRepository
  (clear! [this] (dosync 
                   (ref-set table-map {})))
  (get-table [this table-id] (get @table-map table-id))
  (get-tables [this] (vals @table-map))
  (save-table! [this table] 
    (dosync
      (alter table-map into {(table :id) table})))
  Lockable
  (acquire-lock! [this id]
    (lo/acquire-lock! locks id))
  (release-lock! [this id]
    (lo/release-lock! locks id)))