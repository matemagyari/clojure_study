(ns clojure-study.clojure.macros
  (:use clojure-study.clojure.assertion))


(defn perms [& colls]
  ;vars = d1 d2 d3 ...
  (let [vars (for [i (range (count colls))] (gensym))]
    (interleave vars colls)))



(defmacro permutations [& colls]
  ;vars = d1 d2 d3 ...
  (let [vars (for [i (range (count colls))] (str "d" i))]
    (interleave vars colls)))
;d1 d2 d3 ...
; interleave



