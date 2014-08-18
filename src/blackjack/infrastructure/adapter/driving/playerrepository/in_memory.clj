(ns blackjack.infrastructure.adapter.driving.playerrepository.in-memory
  (:require [blackjack.port.player-repository :refer :all]
            [blackjack.app.lockable :refer :all]
            [blackjack.infrastructure.adapter.driving.shared.locking :as lo]))

(def ^:private player-map (ref {}))
(def ^:private locks (ref {}))

(defrecord InMemoryPlayerRepository []
  PlayerRepository
  (clear! [this] (dosync 
                   (ref-set player-map {})))
  (get-player [this player-id] (get @player-map player-id))
  (get-players [this] (vals @player-map))
  (save-player! [this player] 
    (dosync
      (alter player-map into {(player :id) player})))
  Lockable
  (acquire-lock! [this id]
    (lo/acquire-lock! locks id))
  (release-lock! [this id]
    (lo/release-lock! locks id)))