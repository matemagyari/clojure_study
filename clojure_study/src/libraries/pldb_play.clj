(ns libraries.pldb-play
  (:refer-clojure :exclude [==])
  (:require [clojure.core.logic :refer [run run* membero != conde fresh distincto ==]]
            [clojure.core.logic.pldb :refer [db with-db db-rel db-fact]]
            [clojure-study.assertion :as a]))

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

(defn child [x y]
  (parent y x))

(a/assert= #{[:Cecil :Emil] [:Ben :Dan] [:Abe :Cecil]}
  (set (with-db family
         (run* [x y] (grandfather x y)))))

(a/assert= [:Dan]
  (with-db family
    (run 1 [q] (child q :Cecil))))

;; extend facts - db-facts
;; my father's child, but not me
(let [family (-> (db)
               (db-fact parent :Bob :Mary)
               (db-fact parent :Bob :Lucy))
      result (with-db family
               (run* [q]
                 (fresh [dad]
                   (parent dad :Lucy)
                   (parent dad q)
                   (!= q :Lucy))))]
  (a/assert= result [:Mary]))