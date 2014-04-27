(ns blackjack.table
  (:require [blackjack.events :as events]
            [blackjack.shared :as shared]))

(defn- is-full [table]
  (= 2 (count (table :players))))

(defn create-new-table []
  "creates an empty table"
  {:id (shared/generate-id)})

(defn- check-table-not-full [table]
  (when (is-full table) 
    (shared/raise-domain-exception (str "Table " (table :id) " is full"))))

(defn- check-player-not-seated-yet [table player]
  (when (shared/seq-contains? (table :players) player) 
    (shared/raise-domain-exception (str "Player " player " is already seated at table " (table :id)))))

(defn- table-seating-changed-event [table]
  { :table-id (table :id)
    :players (table :players)
    :type :table-seating-changed-event})

(defn- table-is-full-event [table]
  { :table-id (table :id)
    :type :table-is-full-event})

(defn sit [table player]
  "Seats player to table"
  (check-table-not-full table)
  (check-player-not-seated-yet table player)
  (let [updated-table (update-in table [:players] conj player)]
    (events/publish-event (table-seating-changed-event updated-table))
    (when (is-full table)
      (events/publish-event (table-is-full-event table)))
    updated-table))

(defn clear-table [table]
  "Unseat players"
  (let [updated-table (assoc table :players [])]
    (events/publish-event (table-seating-changed-event updated-table))    
    updated-table))
  
  

