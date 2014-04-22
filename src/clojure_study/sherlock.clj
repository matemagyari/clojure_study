(ns clojure_study.sherlock 
  (:use [clojure.core])
  (:use [clojure.core.logic])
  (:use [clojure.core.logic.pldb]))

;;======================================  facts
(db-rel present Person Room Time)
(def rooms [:foyer :bedroom :library])
(def timerange (range 0 5))
(def persons [:Abe :Bob :Cecil])

(def start
  (db
    [present :Abe :foyer 0]
    [present :Bob :bedroom 0]
    [present :Cecil :library 0]))

(defn move [person oldroom newroom])
  
 (defn turn )
