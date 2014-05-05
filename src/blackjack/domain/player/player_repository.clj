(ns blackjack.domain.player.player-repository
    (:require [blackjack.util.shared :as shared]))

(defprotocol PlayerRepository
  (clear! [this])
  (get-player [this player-id])
  (get-players [this])
  (save-player! [this player]))



