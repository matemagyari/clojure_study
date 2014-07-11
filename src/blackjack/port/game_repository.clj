(ns blackjack.port.game-repository
    (:require [blackjack.util.shared :as shared]))

(defprotocol GameRepository
  (clear! [this])
  (get-game [this game-id])
  (get-games [this])
  (save-game! [this game]))



