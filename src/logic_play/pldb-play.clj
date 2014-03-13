(ns logic_play.core 
 (:refer-clojure :exclude [==])
  (:use [clojure.core.logic])
  (:use [pldb.logic :as pldb]))

;;=========================
(defn assert-equals [actual expected]
  (when-not (= actual expected)
    (throw 
      (AssertionError. 
        (str "Expected " expected " but was " actual)))))



(pldb/db-rel man p)


;;(defrel father Father Child)

;(facts father [[:Vito :Michael]
;               [:Vito :Sonny]
;               ])
;(run* [q]
;      (father :Vito q))
