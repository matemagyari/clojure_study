(ns blackjack.player-repository
    (:require [blackjack.shared :as shared]))

(def player-map (ref {}))

(defprotocol PlayerRepository
  (clear! [this])
  (get-player [this player-id])
  (get-players [this])
  (save-player! [this player]))

(defrecord InMemoryPlayerRepository []
  PlayerRepository
  (clear! [this] (dosync 
                   (ref-set player-map {})))
  (get-player [this player-id] (get @player-map player-id))
  (get-players [this] (vals @player-map))
  (save-player! [this player] 
    (dosync
      (alter player-map into {(player :id) player}))))

(def player-repository (InMemoryPlayerRepository.))




