(ns ^{:author mate.magyari} clojure-study.ideas.closures
  (:require [clojure.test :as test]))

(defn create-id-generator
  "Returns an impure function to generate ids"
  []
  (let [next-id (atom 0)]
    (fn []
      (swap! next-id inc)
      @next-id)))

;; here we create our "object"
(def generate-id! (create-id-generator))

(println "Id: " (generate-id!))
;; Id: 1
(println "Id: " (generate-id!))
;; Id: 2
(println "Id: " (generate-id!))
;; Id: 3

;; here we create another "object"
(def generate-id-2! (create-id-generator))
(println "Id2: " (generate-id-2!))
;; Id2: 1

(defn create-vending-machine
  "Returns an impure function to act as an object representing a vending machine"
  []
  (let [items (atom {:Coke {:quantity 3 :price 5}
                     :Mars {:quantity 2 :price 3}
                     :Sandwich {:quantity 5 :price 10}})
        wallet (atom 0)]
    (fn [item-id money]
      (cond
        (nil? (item-id @items)) :invalid-item
        (zero? (get-in @items [item-id :quantity])) :out-of-stock
        (not= money (get-in @items [item-id :price])) :price-not-match
        :else (do
                (swap! items update-in [item-id :quantity] dec)
                (swap! wallet + money)
                :success)))))

;; the closure, or "object"
(def buy! (create-vending-machine))

(defn assert= [a b]
  (assert (= a b)))

(assert= :invalid-item (buy! :Pepsi 1))
(assert= :price-not-match (buy! :Coke 20))
(assert= :success (buy! :Mars 3))
(assert= :success (buy! :Mars 3))
(assert= :out-of-stock (buy! :Mars 3))