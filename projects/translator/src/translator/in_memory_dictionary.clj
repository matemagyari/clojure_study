(ns translator.in-memory-dictionary
  (:require [translator.domain :as domain]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]))

(s/def ::in-memory-dictionary (s/map-of
                                (s/tuple ::domain/language ::domain/language)
                                (s/map-of ::domain/word ::domain/word)))

(s/fdef read-from-db
        :args (s/cat :m ::in-memory-dictionary
                     :query ::domain/query)
        :ret (s/nilable ::domain/word))
(defn- read-from-db [m query]
  (let [lang-from (get-in query [:query/from ::domain/language])
        lang-to (get query :query/to)
        word-from (get-in query [:query/from ::domain/word])
        dict [lang-from lang-to]]
    (get-in m [dict word-from])))


(defn build-in-memory-dictionary []
  (let [store (atom {})]
    (reify domain/Dictionary
      (translate [this query]
        (read-from-db @store query)))))

