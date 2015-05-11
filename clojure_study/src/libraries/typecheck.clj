(ns libraries.typecheck
  (:import [clojure.lang PersistentHashSet])
  (:require [clojure.core.typed :as typed]))


(println (typed/cf merge-with))
(typed/ann conc [Number Number -> String])
(defn conc [x y]
  (str x y))

;passes
(defn add-test [] (+ 2 3))

;(typed/ann my-first-int [(typed/NonEmptySeqable Number) -> Number])
;(defn my-first-int [nums] (first num))

;; ======== PARAMETERIZED SEQ =============
(typed/ann my-first (typed/All [x]
                      [(typed/NonEmptySeqable x) -> x]))
(defn my-first [a] (first a))


;; ======== VARARGS + NILLABLE =============
(typed/ann first-or-nil [(typed/U nil Number) * -> (typed/U nil Number)])
(defn first-or-nil [& xs]
  (first xs))
(println "first-or-nil: " (first-or-nil))

(typed/ann first-or-one [(typed/U nil Number) * -> Number])
(defn first-or-one [& xs]
  (let [f (first xs)]
      (if f f 2)))

;; ======== TUPLES =============
(typed/ann a-tuple [ Number -> (Vector* Number Number) ])
(defn a-tuple [a] [a a])

(println "first-or-one: " (first-or-one))
(println "first-or-222: " (first []))

;; ======== DEFALIAS =============
(typed/defalias CartesianVector
  (typed/HMap :mandatory {:x Number, :y Number}))

(typed/ann v* [Number CartesianVector ->  CartesianVector])
(defn v* [scalar a-vector]
  {:x (* scalar (:x a-vector))
   :y (* scalar (:y a-vector))})

;; ======== assoc =============
(defn x->CartesianVector [a-map x]
  (assoc a-map :x x))
;; ======== assoc-in =============
(typed/defalias Chapter
  (typed/HMap :mandatory {:title String :pages Number}))
(typed/defalias Book
  (typed/HMap :mandatory {:chapters (typed/NonEmptySeqable Chapter), :title String}))
(defn add-chapter [book chapter]
  (assoc-in book))
;; ======== merge-with =============
(typed/ann x->CartesianVector [(typed/HMap :mandatory {:y Number}) Number ->  CartesianVector])

;; merge-with doesn't work
;(typed/ann v+ [CartesianVector CartesianVector ->  CartesianVector])
;(defn v+ [v1 v2]
;  (merge-with + v1 v2))



;(typed/ann my-count (typed/All [x]
;                      [(typed/NonEmptySeqable x) -> x]))
;(defn my-count [a] (count a))
;fails
;(defn hey [] (+ 2 ""))

;wrong arguments
;(defn bad [] (conc "1" :a))

(comment

  ;fails because it knows the input types for '+' and the output of 'conc'
  (defn bad-2 [a b]
    (+ (conc a b) 3))

  ;
  (defn add [a b] (+ a b))
  (defn bad-3 [a b]
    (add 3 (conc a b)))

  (defn x [a b]
    (if (> 3 4) (conc a b) 7))
  (defn bad-4 []
    (+ 3 (x 2 3)))

  (defn b []
    (let [a-fn conc]
      (apply a-fn [:a :b])))

  (typed/ann ^:no-check fn-applier [typed/IFn typed/HSequential -> typed/Any])
  (defn fn-applier [fn-x args] (apply fn-x args))

  (defn x [& args]
    (fn-applier conc args))
  )

(typed/ann my-inc [Number -> Number])
(defn my-inc [x] (inc x))
(defn sth []
  (let [a (my-inc 2) ; local type inference
        ; [x y] (my-inc 3) ; it would fail because of local type inference deducts that the result is not a vector
        b (my-inc 3)] ; local type inference
    (+ a b)))


;needed otherwise fails (strange '+' works)
(typed/ann ^:no-check clojure.string/blank? [String -> Boolean])

(typed/ann blank? [String -> Boolean])
(defn blank? [x] (clojure.string/blank? x))



(typed/check-ns 'clojure-study.libraries.typecheck)

