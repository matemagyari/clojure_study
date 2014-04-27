(ns blackjack.game-repository
    (:require [blackjack.shared :as shared]))

(def game-repository (ref {}))

(defn- save [game]
  "Saves the game"
  (dosync 
    (into game-repository {(game :id) game})))

(defn- get [game-id]
  "Finds game by id"
  (game-id @game-repository))

(defn get-repository []
  "Game Repository functions"
  {:save save
   :get get})

(defprotocol GameRepository
  (get [this game-id])
  (save [this game]))

(defrecord InMemoryGameRepository
  GameRepository
  (get [this game-id] (game-id @game-repository))
  (save [this game] (dosync 
                      (alter game-repository into {(game :id) game}))))




