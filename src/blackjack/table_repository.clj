(ns blackjack.table-repository
    (:require [blackjack.shared :as shared]))

(def table-repository (ref {}))

(defn- save-table [table]
  "Saves the table"
  (dosync 
    (into table-repository {(table :id) table})))

(defn- get-table [table-id]
  "Finds table by id"
  (table-id @table-repository))

(defprotocol TableRepository
  (get-table [this table-id])
  (save-table [this table]))

(defrecord InMemoryTableRepository []
  TableRepository
  (get-table [this table-id] (get @table-repository table-id))
  (save-table [this table] (dosync 
                           (alter table-repository into {(table :id) table}))))

(defn get-table-repository []
  "Returns the Table Repository"
  (InMemoryTableRepository.))




