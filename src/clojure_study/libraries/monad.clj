(ns clojure-study.libraries.monad
  (:use [clojure-study.assertion]
        [clojure.algo.monads]
        [clojure.core])
  (:require [clojure.repl])
  )

(defmonad my-identity-m
  [m-result identity
   m-bind (fn m-result-id [mv f]
            (f mv))])

;;================= MAYBE MONAD ========================

;;Maybe monad
(defn try-maybe-m [x y]
  (domonad maybe-m
    [a x
     b y]
    (+ a b)))


(assert-equals 5 (try-maybe-m 2 3))
(assert-equals nil (try-maybe-m 2 nil))

(defn try-maybe-m2 [x y]
  (domonad maybe-m
    [a x
     :let [z (inc a)]
     b y]
    (+ a b z)))

(assert-equals 8 (try-maybe-m2 2 3))

(assert-equals nil (with-monad maybe-m
                     (domonad [a 2
                               b nil]
                       (* a b))))


;;================= STATE MONAD ========================
;the followings are m-result functions
(defn double-s [x]
  (fn [state]
    [(* 2 x) (conj state :double)]))

(defn inc-s [x]
  (fn [state]
    [(inc x) (conj state :inc)]))

(defn dec-s [x]
  (fn [state]
    [(dec x) (conj state :dec)]))

;here comes the monad
(defn do-things [x]
  (domonad state-m
    [a (inc-s x)
     b (double-s a)
     c (dec-s b)]
    c))

(def queued-up (do-things 3))

(assert-equals (queued-up []) [7 [:inc :double :dec]])
(assert-equals (queued-up nil) [7 [:dec :double :inc]]) ;because conj works differently in list than in vector
(assert-equals (queued-up '()) [7 [:dec :double :inc]]) ;same
(assert-equals (queued-up #{}) [7 #{:dec :double :inc}])

(clojure.repl/source state-m)

;;================= WRITER MONAD ========================
(defn try-write []
  (domonad (writer-m "?")
    [a (m-result 1)
     _ (write "aa")
     b (m-result 5)
     _ (write "bb")]
    (+ a b)))

(assert-equals [6 "??aa??bb?"] (try-write))

;;================= SEQUENCE MONAD ========================
(defn try-sequence-m [seq-a seq-b]
  (domonad sequence-m
    [x seq-a
     y seq-b]
    (* x y)))

(assert-equals [2 3 4 6 6 9] (take 6 (try-sequence-m [1 2 3] [2 3])))





