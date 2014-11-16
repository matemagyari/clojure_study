(ns clojure-study.libraries.typecheck
  (:require [clojure-study.assertion :as a]
            [clojure.core.typed :as typed]))

(typed/ann conc [Number Number -> String])
(defn conc [x y]
  (str x y))

(defn check [a b]
  (if (not= a b) (throw (RuntimeException.))))

(check "12" (conc 1 2))
;(check "12" (conc "1" "2"))

(defn f0 [] 1)

(typed/ann f1 [Number Number -> String])
(defn f1 [a b]
  (conc a b))

;(def x (f1 1 "2"))

(typed/check-ns 'clojure-study.libraries.typecheck)

