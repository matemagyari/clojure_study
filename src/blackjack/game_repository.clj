(ns blackjack.game-repository
    (:require [blackjack.shared :as shared]))

(def game-map (ref {}))

(defprotocol GameRepository
  (get-game [this game-id])
  (save-game [this game]))

(defrecord InMemoryGameRepository []
  GameRepository
  (get-game [this game-id] (get @game-map game-id))
  (save-game [this game] (dosync 
                           (alter game-map into {(game :id) game}))))

(def game-repository (InMemoryGameRepository.))




