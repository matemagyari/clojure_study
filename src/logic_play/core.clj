(ns logic_play.core 
 (:refer-clojure :exclude [==])
  (:use [clojure.core.logic]))

;;=========================
(defn assert-equals [actual expected]
  (when-not (= actual expected)
    (throw 
      (AssertionError. 
        (str "Expected " expected " but was " actual)))))


(def x (run* [q]
             (membero q [1 2 3])))

(assert-equals x [1 2 3])

(def x (run* [q]
             (membero q [1 2 3 4])
             (membero q [3 4 5 6])))

(assert-equals x [3 4])

(def x (run* [q]
             (membero q [1 2 3])
             (!= q 2)))

(assert-equals x [1 3])

;;conde
(def x (run* [q]
             (conde 
               [(membero q [1 2])]
               [(membero q [3 4])])))

(assert-equals (set x) (set [1 2 3 4]))

;;distincto
(def smurfs [:papa :brainy :lazy])
(def x (run* [q]
             (fresh [smurf1 smurf2]
                    (membero smurf1 smurfs)
                    (membero smurf2 smurfs)
                    (distincto [smurf1 smurf2])
                    (== q [smurf1 smurf2]))))

(assert-equals x [[:papa :brainy] [:brainy :papa] [:papa :lazy] [:lazy :papa] [:brainy :lazy] [:lazy :brainy]])

(println "logic play ended")