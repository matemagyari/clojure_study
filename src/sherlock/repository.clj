(ns
  ^{:author mate.magyari}
  sherlock.repository
  (:refer-clojure :exclude [== <= >=])
  (:require [clojure.core.logic :refer [run run* membero != conde fresh distincto ==]]
            [clojure.core.logic.arithmetic :refer [<= >=]]
            [clojure.core.logic.pldb :refer [db with-db db-rel db-fact]]
            [clojure-study.assertion :as a]))

(db-rel present Person Room Time)
(db-rel open-to RoomA RoomB)


(defn- add-fact [facts & fact-details]
  (apply db-fact (cons facts fact-details)))

(defn empty-db [] (db))

(defn place-person-in-room [facts person room time]
  (add-fact facts present person room time))

(defn define-rooms [facts r1 r2]
  (add-fact facts open-to r1 r2))

(defn adjoining [room-a room-b]
  (conde
    [(open-to room-a room-b)]
    [(open-to room-b room-a)]))

(defn path-1 [r1 r2 r3]
  (adjoining r1 r2)
  (adjoining r2 r3))

(defmacro valid-path? [facts & rooms]
  (let [q (gensym)]
    (loop [acc []
           rooms-t rooms]
      (if (> 2 (count rooms-t)) `(not (empty? (with-db ~facts
                                                (run* [~q]
                                                  ~@acc))))
        (let [r1 (first rooms-t)
              r2 (second rooms-t)
              adjoin-fact (list 'adjoining r1 r2)]
          (recur (cons adjoin-fact acc) (rest rooms-t)))))))

(defmacro theo [facts character start-time end-time]
  (let [time-range (range start-time (inc end-time))
        room-times (for [t time-range] [(gensym) t])
        room-symbols# (map first room-times)
        presents (for [rt room-times]
                   (list 'present character (first rt) (second rt)))]
    (println presents)
    `(with-db ~facts
       (run* [~@room-symbols#]
         ~@presents))))

(let [facts (-> (db) (define-rooms :a :b) (define-rooms :b :c) (define-rooms :b :d))
      test1 (theo facts :Abe 0 3)]
  [test1])

(macroexpand-1 '(theo (db) :Abe 0 3))

(let [facts (-> (db) (define-rooms :a :b) (define-rooms :b :c) (define-rooms :b :d))
      test1 (valid-path? facts :a :b :c)
      test2 (valid-path? facts :a :d)]
  [test1 "a" test2 "b"])

