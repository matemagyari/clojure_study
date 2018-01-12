(ns translator.domain
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]))

(s/def ::word string?)
(s/def ::language string?)

(s/def ::language-and-word (s/tuple ::language ::word))

(s/def :query/from ::language-and-word)
(s/def :query/to ::language)
(s/def ::query (s/keys :req [:query/from :query/to]))

(s/def ::fact (s/tuple ::language-and-word ::language-and-word))

(defprotocol Repository
  (save [this fact])
  (read [this query]))

(defprotocol Dictionary
  (translate [this query]))

(s/def :context/repository #(satisfies? Repository %))
(s/def :context/dictionary #(satisfies? Dictionary %))
(s/def ::context (s/keys :req [:context/repository :context/dictionary]))

; to be able to spec them we need wrapper functions around the repository ones
; it's a bit awkward, because we have do maintain a duplicate of each function

(s/fdef repository-save
        :args (s/cat :repository :context/repository
                     :fact ::fact)
        :ret nil?)
(defn repository-save [repository fact]
  (save repository fact))

(s/fdef repository-read
        :args (s/cat :repository :context/repository
                     :query ::query)
        :ret (s/nilable ::word))
(defn repository-read [repository query]
  (read repository query))



(s/fdef process
        :args (s/cat :query ::query
                     :context ::context)
        :ret (s/nilable ::word))
(defn process [query context]
  (let [repository (:context/repository context)
        dictionary (:context/dictionary context)
        [translated-word state] (as-> (repository-read repository query) $
                                      (or [$ :exists]
                                          [(translate dictionary query) :new]))]
    (when (and (some? translated-word)
               (= :new state))
      (repository-save repository translated-word))

    translated-word))



;(s/def ::good-binary-tree
;  (s/cat :value int?
;         :children (s/map-of #{:left :right} ::good-binary-tree)))
;
;(s/def ::better-binary-tree nil)
;(s/def :btree/value int?)
;(s/def :btree/left ::better-binary-tree)
;(s/def :btree/right ::better-binary-tree)
;
;(s/def ::better-binary-tree
;  (s/keys :req [:btree/value] :opt [:btree/left :btree/right]))
;
;(s/exercise `::better-binary-tree 1)
;
;(s/fdef print-tree
;        :args (s/cat :tree ::good-binary-tree))
;(defn print-tree [tree]
;  (println tree))
;
;(stest/check `print-tree)


