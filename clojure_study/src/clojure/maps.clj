(ns clojure.maps
  (:use clojure-study.clojure.assertion))

;;------------------------------------------------------------------------------- MAPS -------------------------------------------
(def john {:name "John" :age 17})
(def jack {:name "Jack" :age 31})

; keys functions of maps and vice-versa
(assert= (:name john) "John")
(assert= (:name john) (john :name))

;;change map
(assert= (assoc jack
           :gender "M"
           :title "Mr")
  {:name "Jack" :age 31 :gender "M" :title "Mr"})

(assert= (dissoc jack :age)
  {:name "Jack"})
(assert= (select-keys {:a 1 :b 2 :c 3} [:a :c]) {:a 1 :c 3})
(assert= (merge {:a 1 :b 2} {:c 3} {:a 4 :d 5})
  {:a 4 :b 2 :c 3 :d 5})

(assert= (merge-with +
           {:a 1 :b 2}
           {:c 2 :a 4 :d 6})
  {:a 5 :b 2 :c 2 :d 6})

;;modify complex structure
(def person {:name "Joe"
             :address {:street "Camden"
                       :no 12}})

;;get :no field
(assert=
  12
  (get-in person [:address :no]))

;;set :no field
(assert=
  {:name "Joe"
   :address {:street "Camden"
             :no 9}}
  (assoc-in person [:address :no] 9))

;;update :no field with passing a function
(assert=
  {:name "Joe"
   :address {:street "Camden"
             :no 13}}
  (update-in person [:address :no] inc))

