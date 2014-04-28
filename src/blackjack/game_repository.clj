(ns blackjack.game-repository
    (:require [blackjack.shared :as shared]))

(def game-repository (ref {}))

(defn- save-game [game]
  "Saves the game"
  (dosync 
    (into game-repository {(game :id) game})))

(defn- get-game [game-id]
  "Finds game by id"
  (game-id @game-repository))

(defn get-repository []
  "Game Repository functions"
  {:save save-game
   :get get-game})

(defprotocol GameRepository
  (get-game [this game-id])
  (save-game [this game]))

(defrecord InMemoryGameRepository []
  GameRepository
  (get-game [this game-id] (game-id @game-repository))
  (save-game [this game] (dosync 
                           (alter game-repository into {(game :id) game}))))

(defn get-game-repository []
  "Returns the Game Repository"
  (InMemoryGameRepository.))




