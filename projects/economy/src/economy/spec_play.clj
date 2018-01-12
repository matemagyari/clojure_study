(ns economy.spec-play
  (:require [clojure.spec :as s]
            [clojure.spec.test :as stest]
            [clojure.walk :as w]))

(s/def ::id (s/or :num pos-int?
                  :name string?))

(s/def ::age pos-int?)

(s/def ::street string?)
(s/def ::num pos-int?)
(s/def ::address (s/keys :req [::street ::num]))

(s/def ::person (s/keys :req [::id ::age ::address]))

(def p1 {::id 1 ::age 5 ::address {::street "Anker" ::num 5} ::num 16})
(def p2 {::id "joe" ::age 5 ::address {::street "Anker" ::num 5} ::num 16})

(println (s/conform ::person p1))

(defn transform-dispatcher
  "Extracts the spec id from the element if it's available, returns nil otherwise"
  [x]
  (if (and (sequential? x)
           (= 2 (count x))
           (qualified-keyword? (first x)))
    (first x)))

(defmulti transform
          "Transforms a datastructure"
          transform-dispatcher)

(defmethod transform ::id [[_ val]]
  {"id" val})

(defmethod transform ::street [[_ val]]
  {"street" val})

(defmethod transform ::num [[_ val]]
  {"num" val})

(defmethod transform ::address [[_ {street ::street num ::num}]]
  {"Address" (str street " " num)})

(defmethod transform ::age [[_ val]]
  {"Age" val})

;"If the element is not a spec just return it"
(defmethod transform :default [x] x)

(w/prewalk transform p1)


;(def symbols (set (range 20)))
;(def card-size 5)
;
;(defn new-card1 [cards symbols-taken]
;  1)
;
;(defn new-card [card symbols-taken]
;  (let [available-symbols (set/difference card symbols-taken)
;        [common-symbol & others] card]
;    (cons common-symbol (take (dec card-size)
;                              (set/difference symbols symbols-taken card)))))
;
;(defn new-card-2 [cards symbols-taken]
;  (condp = (count cards)
;    0 (take card-size symbols)
;    1 (new-card (first cards) #{})
;    :else (let [[c & cs] cards])))
;
;(defn build-pack [cards]
;  (let []))

