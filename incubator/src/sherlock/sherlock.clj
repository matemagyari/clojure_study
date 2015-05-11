(ns sherlock.sherlock
  (:refer-clojure :exclude [== <= >=])
  (:require [clojure.core.logic :refer [run run* membero != conde fresh distincto ==]]
            [clojure.core.logic.arithmetic :refer [<= >=]]
            [clojure.core.logic.pldb :refer [db with-db db-rel db-fact]]
            [clojure-study.assertion :as a]
            [sherlock.relations :as r]
            [clojure.test :as test]
            [clojure.core.typed :as typed]))

;;========================================

(defn init-characters-positions
  "Returns a world where the characters are placed in rooms a t0"
  [characters rooms facts]
  (if (empty? characters) facts
    (let [character (first characters)
          room (first rooms)
          uf (r/place-person-in-room facts [character room 0])]
      (recur (rest characters) (rest rooms) uf))))


(defn path-of
  "Get the path of a character"
  ([facts person]
    (let [path (with-db facts
                 (run* [r t]
                   (r/rel-present person r t)))]
      (sort #(< (second %1) (second %2)) path)))
  ([facts person till-time]
    (let [path (path-of facts person)]
      (filter #(<= (second %) till-time) path))))

(defn next-random-room [facts person]
  (let [[current-room time] (-> (path-of facts person) last)
        neighbours (r/neighbours facts current-room)
        next-room (rand-nth (cons current-room neighbours))]
    [next-room (inc time)]))

(defn move-character [facts person time]
  (let [[next-room time2] (next-random-room facts person)]
    (a/assert= time time2)
    (r/place-person-in-room facts [person next-room time])))

(defn move-story
  "Moves the world to the next time point"
  [facts characters time]
  (loop [characters-to-move characters
         updated-facts facts]
    (if (empty? characters-to-move) updated-facts
      (let [character (first characters-to-move)
            uf (move-character updated-facts character time)]
        (recur (rest characters-to-move) uf)))))

;;======== queries =========
(defn where-is
  "Returns the room th person occupies at the given time"
  [facts person time]
  (let [[room] (with-db facts
                 (run 1 [r]
                   (r/rel-present person r time)))]
    room))

(defn who-was-there
  "In the given room at a specific time point or in a time window"
  ([facts room time]
    (with-db facts
      (run* [character]
        (r/rel-present character room time))))
  ([facts room start-time end-time]
    (with-db facts
      (run* [character time]
        (r/rel-present character room time)
        (>= time start-time)
        (<= time end-time)))))

(defn get-characters-memory
  "Where was he when and with whom"
  [facts character]
  (for [room-time (path-of facts character)
        :let [other-chars (who-was-there facts (first room-time) (second room-time))]]
    (cons other-chars room-time)))

;(typed/ann memories-of [clojure.lang.PersistentArrayMap -> clojure.lang.PersistentArrayMap])
(defn memories-of
  [stories character]
  "Tracking the character based on other characters' stories"
  (let [with-character? (fn [[characters-in-the-room room time]] ;room-time is a tuple of (characters room time)
                          (some #(= % character) characters-in-the-room))
        individual-stories (for [story stories
                                 :let [relevant-parts (filter with-character? story)]
                                 :when (not (empty? relevant-parts))]
                             relevant-parts)]
    ;(sort-by #(nth % 2) individual-stories)))
    individual-stories))

(defn possible-path-of [stories character]
  "Based on the stories of others the possible path of the character"
  1)

(defn move-story-to
  "move a world it to round t"
  [world persons time]
  (loop [facts world
         t 1]
    (if (> t time) facts
      (recur (move-story facts persons t) (inc t)))))

(defn positions-based-on-stories [stories]
  (let [get-room-time (fn [chars-room-time]
                        [(second chars-room-time) (nth chars-room-time 2)])]
    (->> stories
      (apply concat)
      (map get-room-time)
      (sort #(< (second %1) (second %2)))
      distinct)))

(defn facts-based-on-positions [character positions]
  (loop [facts (r/new-world)
         positions-to-add positions]
    (if (empty? positions-to-add) facts
      (let [position (first positions-to-add)
            room (first position)
            time (second position)
            updated-facts (r/place-person-in-room facts [character room time])]
        (recur updated-facts (rest positions-to-add))))))

(defn theories [character facts]
  (with-db facts
    (run* [room-1 room-2 time-1 time-2]
      (r/rel-present character room-1 time-1)
      (r/rel-present character room-2 time-2)
      (clojure.core.logic.arithmetic/< time-1 time-2)
      ;(clojure.core.logic.arithmetic/= time-2 (inc time-1))
      (conde
        [(clojure.core.logic.arithmetic/= 0 time-1) (clojure.core.logic.arithmetic/= 1 time-2)]
        [(clojure.core.logic.arithmetic/= 1 time-1) (clojure.core.logic.arithmetic/= 2 time-2)]
        [(clojure.core.logic.arithmetic/= 2 time-1) (clojure.core.logic.arithmetic/= 3 time-2)]
        [(clojure.core.logic.arithmetic/= 3 time-1) (clojure.core.logic.arithmetic/= 4 time-2)]
        [(clojure.core.logic.arithmetic/= 4 time-1) (clojure.core.logic.arithmetic/= 5 time-2)]
        [(clojure.core.logic.arithmetic/= 5 time-1) (clojure.core.logic.arithmetic/= 6 time-2)]
        )
      (r/rel-adjoining room-1 room-2)
      ;(<= time-2 10)
      )))


;(def theos (theories :Cecil facts))

;(who-was-there-between w :dining-room 0 5)

;================ tests ===================
(defn is= [x y]
  (test/is (= x y)))

(defn is-not [x]
  (test/is (false? x)))

(defn create-house [world]
  (-> world
    (r/define-rooms :foyer :dining-room)
    (r/define-rooms :foyer :kitchen)
    (r/define-rooms :dining-room :living-room)
    (r/define-rooms :kitchen :living-room)))

(def rooms [:foyer :dining-room :kitchen :living-room])
(def persons [:Abe :Bob :Cecil])

(def a-world
  (let [room-list (repeatedly #(rand-nth rooms))]
    (as-> (r/new-world) $
      (create-house $)
      (init-characters-positions persons room-list $)
      (move-story-to $ persons 6))))

(println :Bob (path-of a-world :Bob))
(println :Abe (path-of a-world :Abe))
(println :Cecil (path-of a-world :Cecil))

(def facts (let [abes-story (get-characters-memory a-world :Abe)
                 bobs-story (get-characters-memory a-world :Bob)
                 mems-of-Cecil (memories-of [abes-story bobs-story] :Cecil)
                 positions (positions-based-on-stories mems-of-Cecil)
                 facts (facts-based-on-positions :Cecil positions)]
             (println "mems-of-Cecil" mems-of-Cecil)
             (println "positions" positions)
             (println "facts" facts)
             (->> facts create-house (init-characters-positions persons rooms))))

(println "Facts:\n" facts)

(println (class a-world))
(typed/check-ns 'sherlock.sherlock)




