(ns libraries.logic_play
  (:refer-clojure :exclude [== record?])
  (:require [clojure.core.logic :refer [run* membero != conde fresh distincto ==]
             :exclude [record?]]
            [clojure.core.logic.pldb :as pl]
            [clojure-study.assertion :as a]))

;;=========================
;; membero - simple
(a/assert= [1 2 3]
  (run* [q]
    (membero q [1 2 3])))

(a/assert= [3 4]
  (run* [q]
    (membero q [1 2 3 4])
    (membero q [3 4 5 6])))

;;!=
(a/assert= [1 3]
  (run* [q]
    (membero q [1 2 3])
    (!= q 2)))

;;conde
(a/assert= (set [1 2 3 4])
  (set (run* [q]
         (conde
           [(membero q [1 2])]
           [(membero q [3 4])]))))

;;distincto and fresh
(let [smurfs [:papa :brainy :lazy]
      result (run* [q]
               (fresh [smurf1 smurf2]
                 (membero smurf1 smurfs)
                 (membero smurf2 smurfs)
                 (distincto [smurf1 smurf2])
                 (== q [smurf1 smurf2])))]
  (a/assert= result
    [[:papa :brainy] [:brainy :papa] [:papa :lazy] [:lazy :papa] [:brainy :lazy] [:lazy :brainy]]))



