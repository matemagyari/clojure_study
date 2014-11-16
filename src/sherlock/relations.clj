(ns
  ^{:author mate.magyari}
  sherlock.relations
  (:require [clojure.core.logic :as logic]
            [clojure.core.logic.pldb :as pldb]
            [clojure-study.assertion :as a]))

;================ relations ===================
;the Person is in the Room at Time
(pldb/db-rel rel-present Person Room Time)
;RoomTo is accessible from RoomFrom
(pldb/db-rel rel-open-to FromRoom ToRoom)


;================ derived relations ===================
(defn rel-adjoining [room-a room-b]
  (logic/conde
    [(rel-open-to room-a room-b)]
    [(rel-open-to room-b room-a)]))

(defn path-1 [r1 r2 r3]
  (rel-adjoining r1 r2)
  (rel-adjoining r2 r3))

;================ functions ===================
(defn- add-fact [old-facts & new-fact-details]
  (apply pldb/db-fact (cons old-facts new-fact-details)))

(defn new-world [] (pldb/db))

(defn place-person-in-room [world [person room time]]
  (add-fact world rel-present person room time))

(defn define-rooms [world from-room to-room]
  (add-fact world rel-open-to from-room to-room))

;;query functions

(defn neighbours [facts room]
  (pldb/with-db facts
    (logic/run* [q]
      (rel-adjoining room q))))

(defn neighbours? [world room-1 room-2]
  (let [qs (pldb/with-db world
              (logic/run* [q]
                (rel-adjoining room-1 room-2)))]
    (-> qs empty? not)))

(defmacro ^:private valid-path?-mac [world & rooms]
  (let [q (gensym)]
    (loop [acc []
           rooms-t rooms]
      (cond
        (> 2 (count rooms-t))
        `(not (empty? (pldb/with-db ~world
                        (logic/run* [~q]
                          ~@acc))))
        :else (let [r1 (first rooms-t)
                    r2 (second rooms-t)
                    adjoin-fact (list 'rel-adjoining r1 r2)]
                (recur (cons adjoin-fact acc) (rest rooms-t)))))))


(defn valid-path? [world & rooms]
  (cond
    (->> rooms count (> 2))
    true
    (neighbours? world (first rooms) (second rooms))
    (recur world (rest rooms))
    :else false))

(def x (-> (new-world) (define-rooms :r1 :r2) (define-rooms :r2 :r3) (define-rooms :r3 :r4)))
(valid-path?-mac x :r2 :r4)
(valid-path? x :r2 :r3)

(defmacro theo [facts character start-time end-time]
  (let [time-range (range start-time (inc end-time))
        room-times (for [t time-range] [(gensym) t])
        room-symbols# (map first room-times)
        presents (for [rt room-times]
                   (list 'rel-present character (first rt) (second rt)))]
    (println presents)
    `(pldb/with-db ~facts
       (logic/run* [~@room-symbols#]
         ~@presents))))

(let [facts (-> (pldb/db) (define-rooms :a :b) (define-rooms :b :c) (define-rooms :b :d))
      test1 (theo facts :Abe 0 3)]
  [test1])

(macroexpand-1 '(theo (pldb/db) :Abe 0 3))

(let [facts (-> (pldb/db) (define-rooms :a :b) (define-rooms :b :c) (define-rooms :b :d))
      test1 (valid-path? facts :a :b :c)
      test2 (valid-path? facts :a :d)]
  [test1 "a" test2 "b"])

