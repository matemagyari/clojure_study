(ns clojure-study.maps
  (:use clojure-study.assertion))

;;------------------------------------------------------------------------------- MAPS -------------------------------------------
(def john {:name "John" :age 17}) 
(def jack {:name "Jack" :age 31})

; keys functions of maps and vice-versa
(assert-equals (:name john) "John")
(assert-equals (:name john) (john :name))

;;change map
(assert-equals (assoc jack 
                      :gender "M" 
                      :title "Mr")
               {:name "Jack" :age 31 :gender "M" :title "Mr"}) 
(assert-equals (dissoc jack :age)
               {:name "Jack"})
(assert-equals (select-keys {:a 1 :b 2 :c 3} [:a :c]) {:a 1 :c 3})
(assert-equals (merge {:a 1 :b 2} {:c 3} {:a 4 :d 5})
               {:a 4 :b 2 :c 3 :d 5})

(assert-equals (merge-with + 
                           {:a 1 :b 2} 
                           {:c 2 :a 4 :d 6})
               {:a 5 :b 2 :c 2 :d 6})
