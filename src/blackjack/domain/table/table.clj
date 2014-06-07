(ns blackjack.domain.table.table
  (:require [blackjack.util.shared :as s]))

(defn- is-full [table]
  (= 2 (count (table :players))))

(defn create-new-table []
  "creates an empty table"
  {:id (s/generate-id) :players [] :events []})

(defn- check-table-not-full [table]
  (when (is-full table)
    (s/raise-domain-exception (str "Table " (table :id) " is full"))))

(defn- check-player-not-seated-yet [table player]
  (when (s/seq-contains? (table :players) player)
    (s/raise-domain-exception (str "Player " player " is already seated at table " (table :id)))))

(defn- table-seating-changed-event [table]
  {:table-id (table :id)
   :players (table :players)
   :type :table-seating-changed-event})

(defn- table-is-full-event [table]
  {:table-id (table :id)
   :players (table :players)
   :type :table-is-full-event})


(defn- add-player [table player]
  (update-in table [:players] conj player))

(defn sit [table player]
  "Seats player to table"
  (check-table-not-full table)
  (check-player-not-seated-yet table player)
  (let [t1 (add-player table player)
        t2 (s/add-event t1
             (table-seating-changed-event t1))]
    (if (is-full t2)
      (s/add-event t2
        (table-is-full-event t2))
      t2)))


(defn clear-table [table]
  "Unseat players"
  (let [updated-table (assoc table :players [])
        event (table-seating-changed-event updated-table)]
    (s/add-event updated-table event)))
  
  

