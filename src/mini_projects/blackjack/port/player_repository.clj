(ns mini-projects.blackjack.port.player-repository
    (:require [mini-projects.blackjack.util.shared :as shared]))

(defprotocol PlayerRepository
  (clear! [this])
  (get-player [this player-id])
  (get-players [this])
  (save-player! [this player]))



