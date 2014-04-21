(ns blackjack.game
  (:use clojure-study.assertion))

(def suites [:club :heart :spade :diamond])
(def ranks [:2 :3 :4 :5 :6 :7 :8 :9 :10 :J :Q :K :A])

(def new-deck
  (shuffle 
    (for [suite suites
          rank ranks]
      [suite rank])))

(defn draw [deck]
  [(first deck) (rest deck)])

;;(defprotocol GameBehaviour
;;  (hit [this player])
;;  (stand [this player]))

;;(defrecord Game [dealer player]
;;  GameBehaviour
;;  (hit [this player] (this)))