(ns carfactory.async-play
  (:require [clojure.core.async :as async]))

(defn work [r]
  (println "Start\n")
  (Thread/sleep 5000)
  (println "End\n")
  r)

(defn long-comp1 [] (work 5))
(defn long-comp2 [] (work 6))

(defn aggregate [xs]
  (println xs))

(let [c1 (async/go (long-comp1))
      c2 (async/go (long-comp2))]
  (async/go
    (aggregate [(async/<! c1) (async/<! c2)]))
  (println "Let's roll!"))

(defn fs->chans
  "Returns channels holding the result of each function execution"
  [fs]
  (for [f fs] (async/go (f))))

(let [channels (fs->chans [long-comp1 long-comp2])]
  (async/go
    (aggregate (map async/<!! channels)))
  (println "Let's roll!"))


(Thread/sleep 1000)

