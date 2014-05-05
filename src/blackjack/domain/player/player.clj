(ns blackjack.domain.player.player
    (:require [blackjack.util.shared :as shared]))

(defn create-player [name]
  "Creates a new player"
  {:id (shared/generate-id) 
   :name name
   :win-number 0})

(defn record-win [player]
  "Records one more win"
  (update-in player [:win-number] inc))