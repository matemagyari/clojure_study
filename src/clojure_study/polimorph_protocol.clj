(ns clojure-study.polimorph-protocol
  (:use clojure-study.assertion))

;;extend existing types with protocol
(defprotocol Whoami
  (whoami [this]))

(extend-protocol Whoami
  java.lang.Boolean
  (whoami [this]
    (str "I'm a boolean: " this))
  clojure.lang.Keyword
  (whoami [this]
    (str "I'm a keyword: " this))
  nil
  (whoami [this]
    "I'm a null"))

(assert-equals "I'm a boolean: true" (whoami true))
(assert-equals "I'm a keyword: :a" (whoami :a))
(assert-equals "I'm a null" (whoami nil))


