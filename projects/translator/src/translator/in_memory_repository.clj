(ns translator.in-memory-repository
  (:require [translator.domain :as domain]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]))

(s/def ::in-memory-db (s/map-of
                        (s/tuple ::domain/language ::domain/language)
                        (s/map-of ::domain/word ::domain/word)))

(def example
  {["english" "german"] {"play" "spielen"
                         "boy"  "junge"}

   ["german" "english"] {"wagen" "car"
                         "katze" "cat"}})

(s/fdef save-in-db
        :args (s/cat :m ::in-memory-db
                     :fact ::domain/fact)
        :ret ::in-memory-db)
(defn- save-in-db [m fact]
  (let [[[from-lang from-word] [to-lang to-word]] fact
        dict [from-lang to-lang]]
    (assoc-in m [dict from-word] to-word)))

(s/fdef read-from-db
        :args (s/cat :m ::in-memory-db
                     :query ::domain/query)
        :ret (s/nilable ::domain/word))
(defn- read-from-db [m query]
  (let [lang-from (get-in query [:query/from ::domain/language])
        lang-to (get query :query/to)
        word-from (get-in query [:query/from ::domain/word])
        dict [lang-from lang-to]]
    (get-in m [dict word-from])))


(defn build-in-memory-repository []
  (let [store (atom {})]
    (reify domain/Repository
      (save [this fact]
        (swap! store save-in-db fact))
      (read [this query]
        (read-from-db @store query)))))

;(stest/check `save-in-db)
;(stest/check `read-from-db)