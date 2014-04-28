(ns blackjack.player-repository
    (:require [blackjack.shared :as shared]))

(def player-repository (ref {}))

(defn- save-player [player]
  "Saves the player"
  (dosync 
    (into player-repository {(player :id) player})))

(defn- get-player [player-id]
  "Finds player by id"
  (player-id @player-repository))

(defprotocol PlayerRepository
  (get-player [this player-id])
  (save-player [this player]))

(defrecord InMemoryPlayerRepository []
  PlayerRepository
  (get-player [this player-id] (get @player-repository player-id))
  (save-player [this player] (dosync 
                           (alter player-repository into {(player :id) player}))))

(defn get-player-repository []
  "Returns the Player Repository"
  (InMemoryPlayerRepository.))




