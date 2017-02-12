(ns libraries.monads-play
  (:require [clojure.repl :as r]
            [clojurelang.assertion :as a]
            [clojure.algo.monads :as m]))

(m/defmonad my-identity-m
  [m-result identity
   m-bind (fn m-result-id [mv f]
            (f mv))])

;http://stackoverflow.com/questions/9881471/map-and-reduce-monad-for-clojure-what-about-a-juxt-monad
; m-result: wraps a value into a monad
; m-bind: 1. unwraps mv (monadic value) to get v, 2. applies f (normal function) to v to create a new monadic value

(defn dot-result
  "Transforms a number to a sequence of dots - a value to a monadic value"
  [v]
  (apply str
    (repeat v \.)))

(defn dot-bind
  "mv - monadic value, a sequence of dots. f - a function. applies f to the length of mv"
  [mv f]
  (f (count mv)))

(m/defmonad dot-monad
  [m-result dot-result
   m-bind dot-bind])

(a/assert= "..."
  (m/domonad dot-monad
    [a "."
     b ".."]
    (+ a b)))

;;================ M-LIFT =================================

(def subtract-dots
  (m/with-monad dot-monad
    (m/m-lift 2 -)))

(a/assert= ".." (subtract-dots "....." "..."))

;;without lift
(defn dup-dots [a]
  (m/domonad dot-monad
    [x a] ; transforms a number to monad
    (* 2 a)))

(a/assert= ".." (dup-dots "."))

;;Monadic Laws
;;Identity - (m-bind (m-result x) f) is equal to (f x)
(defn satisfies-law-of-identity? [m-bind m-result v f]
  (= (m-bind (m-result v) f) (f v)))
;;Reverse Identity - (m-bind mv m-result) is equal to mv where mv is a monadic value.
(defn satisfies-law-of-reverse-identity? [m-bind m-result mv]
  (= (m-bind mv m-result) mv))
;;Associativity - (m-bind (m-bind mv f) g) is equal to (m-bind mv (fn [x] (m-bind (f x) g))) 
;; where f and g are monadic functions and mv is a monadic value.
(defn satisfies-law-of-associativity? [m-bind mv f g]
  (= (m-bind (m-bind mv f) g)
    (m-bind mv (fn [x] (m-bind (f x) g)))))

(assert (satisfies-law-of-identity? dot-bind dot-result 4 inc))
(assert (satisfies-law-of-identity? dot-bind dot-result 4 dec))
(assert (satisfies-law-of-reverse-identity? dot-bind dot-result ".."))
;(assert (satisfies-law-of-associativity? dot-bind ".." dup-dots dup-dots))

;;================= NO-NIL-MONAD
(m/defmonad no-nil-monad
  [m-result (fn [v] v)
   m-bind (fn [mv f]
            (if (nil? mv) nil (f mv)))])

(defn fragile+ [a b]
  (+ a b))

;;any function can be made nil-safe!
(def safe+ (m/with-monad no-nil-monad
             (m/m-lift 2 fragile+)))

(a/assert= 4 (safe+ 3 1))
(a/assert= nil (safe+ nil 1))


;;================= MAYBE MONAD ========================

;;Maybe monad
(defn try-maybe-m [x y]
  (m/domonad m/maybe-m
    [a x
     b y]
    (+ a b)))


(a/assert= 5 (try-maybe-m 2 3))
(a/assert= nil (try-maybe-m 2 nil))

(defn try-maybe-m2 [x y]
  (m/domonad m/maybe-m
    [a x
     :let [z (inc a)]
     b y]
    (+ a b z)))

(a/assert= 8 (try-maybe-m2 2 3))

(a/assert= nil (m/with-monad m/maybe-m
                 (m/domonad [a 2
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
  (m/domonad m/state-m
    [a (inc-s x)
     b (double-s a)
     c (dec-s b)]
    c))

(def queued-up (do-things 3))

(a/assert= (queued-up []) [7 [:inc :double :dec]])
(a/assert= (queued-up nil) [7 [:dec :double :inc]]) ;because conj works differently in list than in vector
(a/assert= (queued-up '()) [7 [:dec :double :inc]]) ;same
(a/assert= (queued-up #{}) [7 #{:dec :double :inc}])

(r/source state-m)

;;================= WRITER MONAD ========================
(defn try-write []
  (m/domonad (m/writer-m "?")
    [a (m/m-result 1)
     _ (m/write "aa")
     b (m/m-result 5)
     _ (m/write "bb")]
    (+ a b)))

(a/assert= [6 "??aa??bb?"] (try-write))

;;================= SEQUENCE MONAD ========================
(defn try-sequence-m [seq-a seq-b]
  (m/domonad m/sequence-m
    [x seq-a
     y seq-b]
    (* x y)))

(a/assert= [2 3 4 6 6 9] (take 6 (try-sequence-m [1 2 3] [2 3])))





