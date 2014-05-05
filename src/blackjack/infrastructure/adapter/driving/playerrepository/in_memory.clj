(ns blackjack.infrastructure.adapter.driving.playerrepository.in-memory
  (:use [blackjack.domain.player.player-repository]))

(def player-map (ref {}))

(defrecord InMemoryPlayerRepository []
  PlayerRepository
  (clear! [this] (dosync 
                   (ref-set player-map {})))
  (get-player [this player-id] (get @player-map player-id))
  (get-players [this] (vals @player-map))
  (save-player! [this player] 
    (dosync
      (alter player-map into {(player :id) player}))))