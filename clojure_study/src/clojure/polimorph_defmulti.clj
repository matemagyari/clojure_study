(ns clojure.polimorph-defmulti
  (:require [clojure-study.clojure.assertion :as ae]))


;; defmulti and defmethod signatures
;; (defmulti method-name dispatcher-function)
;; (defmethod method-name dispatcher-value)

;; defmulti based on a field - the dispatching function is :shape (which works as a function on maps)
(defmulti area :shape)

(defmethod area :square [sq]
  (* (:side sq) (:side sq)))

(defmethod area :circle [circ]
  (* 3.14 (:rad circ) (:rad circ)))

;; default option
(defmethod area :default [_] :no-idea)


(ae/assert= 100 (area {:shape :square :side 10}))
(ae/assert= 314.0 (area {:shape :circle :rad 10}))
(ae/assert= :no-idea (area {:shape :deltoid}))

;; defmulti with a 'real' dispatcher funtion

(defmulti measure
          (fn [x] (> 10 x)))

(defmethod measure true [_]
  "Less than 10")
(defmethod measure false [_]
  "Greater than 10")

(ae/assert= "Greater than 10" (measure 11))
(ae/assert= "Less than 10" (measure 9))


;; defmulti with multiple arguments

(defmulti calculate (fn [op x y] (:type op)))

(defmethod calculate :plus [op x y]
  (+ x y))

(defmethod calculate :minus [op x y]
  (- x y))

(defmethod calculate :default [op x y]
  (* x y))

(ae/assert= 5 (calculate {:type :plus} 2 3))
(ae/assert= 3 (calculate {:type :minus} 7 4))
(ae/assert= 28 (calculate {:type :unknown} 7 4))

;polymorphism cheat
(def data-source :imdb)

; persistence functions
(defmulti get-movie (fn [_] data-source))
(defmulti get-friends-ids (fn [_] data-source))

(def imdb-db {1 {:name    :joe
                 :friends [2 3]}
              2 {:name    :jane
                 :friends [1 3]}
              3 {:name    :jack
                 :friends [1 2]}})

(def rotten-tomatoes-db {1 {:name    :abe
                            :friends [2 3]}
                         2 {:name    :anna
                            :friends [1 3]}
                         3 {:name    :albert
                            :friends [1 2]}})

(defmethod get-movie :imdb [id] (get-in imdb-db [id :name]))
(defmethod get-friends-ids :imdb [id] (get-in imdb-db [id :friends]))

(defmethod get-movie :db [id] (get-in rotten-tomatoes-db [id :name]))
(defmethod get-friends-ids :db [id] (get-in rotten-tomatoes-db [id :friends]))

(defn get-friends [user-id]
  (map get-movie (get-friends-ids user-id)))

(println "Result" (get-friends 1))

;;Dependency injection in Clojure

(comment
  "In OO - and especially in the Java - world Dependency Injection has become mainstream in the last decade,
  and with good reason. As a convert to functional languages I'd like to port most of my toolset I've
  collected so far programming Java, therefore in the followings I'm gonna meditate on how DI
  could be implemented in Clojure. Actually, first I'll just think about whether it's worth doing it at all.
  Let's start with thinking about the benefits of DI in OO. Off the top of my head I see 3
  1. Single responsibility principle - the UserService is using UserDataSource. Using an object and
  creating it is too independent responsibility (and code).
  2. Polymorphism.... It doesn't even have to know that it's a database underneath (data might come from a file for
  all it cares about).
  2. Testability. UserService can be tested without DB connection by stubbing out UserDataSource

  Before trying to figure out how DI could be implemented in Clojure we'd better answer the question.

  1. L")

;3 benefits of DI
;1. loose coupling - e.g. hiding IMDB access behind an interface
;   we can reuse of CinemaStatistic app using Rotten tomatoes instead (some adapter needed)
;   also ComponentA doesn't have to create ComponentB, just what it does. Need to know prinicple is very important in software
;2. testability
;a
;1. Functions don't need to be created, they are like static methods in java.
; if function1 uses function2 it doesn't need to 'create' it
; 2. in Clojure rebinding functions during test is trivial (with-redefs)
;a
;a

