(ns blackjack.infrastructure.adapter.driving.gamerepository.in-memory
  (:use [blackjack.domain.game.game-repository]))

(def game-map (ref {}))

(defrecord InMemoryGameRepository []
  GameRepository
  (clear! [this] (dosync 
                   (ref-set game-map {})))
  (get-game [this game-id] (get @game-map game-id))
  (get-games [this] (vals @game-map))
  (save-game! [this game] 
    (dosync
      (alter game-map into {(game :id) game}))))