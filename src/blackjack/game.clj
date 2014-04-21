(ns blackjack.game
  (:use clojure-study.assertion))

(def suites [:club :heart :spade :diamond])
(def rank-value [[:2 2]
                 [:3 3]
                 [:4 4]
                 [:5 5]
                 [:6 6]
                 [:7 7]
                 [:8 8]
                 [:9 9]
                 [:10 10]
                 [:J 10]
                 [:Q 10]
                 [:K 10]
                 [:A 11]])
(def ranks (map first rank-value))

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
  (let [[card deck] (draw (game :deck))]
    (-> game
        (update-in [player :cards] #(cons card %))
        (assoc :deck deck)
        (assoc :last-to-act player))))


(defn stand [game player]
  (assoc-in game [player :state] :stand))

(defn out-of-turn [game player]
  (= player (:last-to-act game)))

;;(defprotocol GameBehaviour
;;  (hit [this player])
;;  (stand [this player]))

;;(defrecord Game [dealer player]
;;  GameBehaviour
;;  (hit [this player] (this)))