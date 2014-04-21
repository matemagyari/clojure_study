(ns blackjack.game
  (:use clojure-study.assertion))

(def suites [:club :heart :spade :diamond])
(def ranks [:2 :3 :4 :5 :6 :7 :8 :9 :10 :J :Q :K :A])

(defn new-deck []
  (shuffle 
    (for [suite suites
          rank ranks]
      [suite rank])))

(defn draw [deck]
  [(first deck) (rest deck)])

(defn new-game [dealer player]
  {player { :cards #{} }
   dealer { :cards #{} }
   :deck (take 5 (new-deck))})

(defn hit [game player]
  (let [[card deck] (draw (game :deck))
        g1 (update-in game [player :cards] #(cons card %))
        g2 (assoc g1 :deck deck)]
    g2))

(defn stand [game player]
  (assoc-in game [player :state] :stand))

;;(defprotocol GameBehaviour
;;  (hit [this player])
;;  (stand [this player]))

;;(defrecord Game [dealer player]
;;  GameBehaviour
;;  (hit [this player] (this)))