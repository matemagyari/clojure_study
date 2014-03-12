(ns logic_play.core 
 (:refer-clojure :exclude [==])
  (:use [clojure.core.logic]))

(println "logic play started")

(run* [q]
  (== q 1))

(println "logic play ended")