(ns sherlock.sherlock
  (:refer-clojure :exclude [== <= >=])
  (:require [clojure.core.logic :refer [run run* membero != conde fresh distincto ==]]
            [clojure.core.logic.arithmetic :refer [<= >=]]
            [clojure.core.logic.pldb :refer [db with-db db-rel db-fact]]
            [clojure-study.assertion :as a]
            [sherlock.relations :as r]))

;;========================================
(def rooms [:foyer :dining-room :kitchen :living-room])
(def persons [:Abe :Bob :Cecil])

(defn create-house [facts]
  (-> facts
    (r/define-rooms :foyer :dining-room)
    (r/define-rooms :foyer :kitchen)
    (r/define-rooms :dining-room :living-room)
    (r/define-rooms :kitchen :living-room)))

(defn init-characters-positions [characters rooms facts]
  (if (empty? characters) facts
    (let [character (first characters)
          room (rand-nth rooms)
          uf (r/place-person-in-room facts [character room 0])]
      (recur (rest characters) rooms uf))))



(defn init-story [persons rooms]
  (->> (r/new-world) create-house (init-characters-positions persons rooms)))

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
    (a/assert-equals time time2)
    (r/place-person-in-room facts [person next-room time])))

(defn move-story [facts characters time]
  (loop [characters-to-move characters
         updated-facts facts]
    (if (empty? characters-to-move) updated-facts
      (let [character (first characters-to-move)
            uf (move-character updated-facts character time)]
        (recur (rest characters-to-move) uf)))))

;;======== queries =========
(defn where-is [facts person time]
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

(defn memories-of [stories character]
  "Tracking the character based on other characters' stories"
  (let [with-character? (fn [[characters-in-the-room room time]] ;room-time is a tuple of (characters room time)
                          (some #(= % character) characters-in-the-room))
        individual-stories (for [story stories
                                 :let [relevant-parts (filter with-character? story)]
                                 :when (not (empty? relevant-parts))]
                             relevant-parts)]
    individual-stories))

(defn possible-path-of [stories character]
  "Based on the stories of others the possible path of the character"
  1)

(defn move-story-to [person rooms time]
  "init a story and move it to round t"
  (loop [facts (init-story persons rooms)
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

(def w (move-story-to persons rooms 6))

(println :Bob (path-of w :Bob))
(println :Abe (path-of w :Abe))
(println :Cecil (path-of w :Cecil))

(def facts (let [abes-story (get-characters-memory w :Abe)
                 bobs-story (get-characters-memory w :Bob)
                 mems-of-Cecil (memories-of [abes-story bobs-story] :Cecil)
                 positions (positions-based-on-stories mems-of-Cecil)
                 facts (facts-based-on-positions :Cecil positions)]
             (println "mems-of-Cecil" mems-of-Cecil)
             (println "positions" positions)
             (println "facts" facts)
             (->> facts create-house (init-characters-positions persons rooms))))

;(def theos (theories :Cecil facts))

;(who-was-there-between w :dining-room 0 5)




