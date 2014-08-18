(ns
  ^{:author mate.magyari}
  clojure-study.libraries.transducers-play
  (:require [clojure-study.assertion :as ae]))

;;simple transducer
(let [increment (map inc)
      r (sequence increment [1 2 3])
      r1 (into [] increment [1 2 3])
      r2 (transduce increment + [1 2 3])]
  (ae/assert-equals (take 3 r) [2 3 4])
  (ae/assert-equals r1 [2 3 4])
  (ae/assert-equals r2 9))

;;complex transducer
(let [increment-even (comp
                  (filter even?)
                  (map inc))
      r1 (into [] increment-even [1 2 3 4])]
  (ae/assert-equals r1 [3 5]))


(defn -main []
  (println "hi"))
