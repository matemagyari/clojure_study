(ns mini-projects.blackjack.infrastructure.adapter.driving.gamerepository.in-memory
  (:import [java.util ArrayList]
           [java.util.concurrent ConcurrentHashMap])
  (:require [mini-projects.blackjack.port.game-repository :refer :all]
            [mini-projects.blackjack.app.lockable :refer :all]
            [mini-projects.blackjack.infrastructure.adapter.driving.shared.locking :as lo]))

(def ^:private game-map (ref {}))
(def ^:private locks (ref {}))

(defrecord InMemoryGameRepository []
  GameRepository
  (clear! [this] (dosync 
                   (ref-set game-map {})))
  (get-game [this game-id] (get @game-map game-id))
  (get-games [this] (vals @game-map))
  (save-game! [this game] 
    (dosync
      (alter game-map into {(game :id) game})))
  Lockable
  (acquire-lock! [this id]
    (lo/acquire-lock! locks id))
  (release-lock! [this id]
    (lo/release-lock! locks id)))