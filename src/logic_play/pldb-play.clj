(ns pldb_play.core 
 (:refer-clojure :exclude [==])
  (:use [clojure.core.logic])
  (:use [clojure.core.logic.pldb]))

;;=========================
(defn assert-equals [actual expected]
  (when-not (= actual expected)
    (throw 
      (AssertionError. 
        (str "Expected " expected " but was " actual)))))


;;======================================  facts
(db-rel parent Father Child)

(def family
  (db
    [parent :Abe :Ben]
    [parent :Ben :Cecil]
    [parent :Cecil :Dan]
    [parent :Dan :Emil]))

(def father-sons
  (with-db family
    (run* [a-father a-son] (parent a-father a-son))))

(defn grandfather [x y]
  (fresh [z]
    (parent x z)
    (parent z y)))

(def gfather-gsons
  (with-db family
    (run* [x y] (grandfather x y))))

(assert-equals gfather-gsons [[:Cecil :Emil] [:Ben :Dan] [:Abe :Cecil]])

(defn child [x y]
    (parent y x))

(def x 
  (with-db family
    (run 1 [q] (child q :Cecil))))

(assert-equals x '(:Dan))


;; extend facts - db-facts
(def facts
  (-> family
    (db-fact parent :Bob :Mary)
    (db-fact parent :Bob :Lucy)))

;; my father's child, but not me
(def x 
  (with-db facts
    (run* [q]
          (fresh [dad]
                (parent dad :Lucy)
                (parent dad q )
                (!= q :Lucy)))))

(assert-equals x '(:Mary))

(defn who-is-it? [me] 
  (with-db facts
    (run* [q]
          (fresh [dad]
                (parent dad me)
                (parent dad q )
                (!= q me)))))

(assert-equals (who-is-it? :Lucy) '(:Mary))
(assert-equals (who-is-it? :Mary) '(:Lucy))