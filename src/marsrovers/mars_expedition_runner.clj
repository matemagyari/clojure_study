(ns
  ^{:author mate.magyari
    :doc "Expedition runner"}
  marsrovers.mars-expedition-runner
  (:require [clojure.core.async :as a]
            [marsrovers.app :as app]
            [marsrovers.glue :as glue]
            [marsrovers.expedition-config-reader :as ecr]))

(def displayer-channel (glue/chan))
(def plateau-channel (glue/chan))
(def nasa-hq-channel (glue/chan))

(def time-stamp (System/currentTimeMillis))
(println "Reading up expedition config...")
(def expedition-config (ecr/expedition-config))
(def dim-screen [600 600])

(println (str (- (System/currentTimeMillis) time-stamp) " ms has elapsed"))
(println "Word starting...")
(app/start-world! expedition-config plateau-channel nasa-hq-channel displayer-channel dim-screen)
(println "Word started")

(app/start-rovers!
  (:rover-configs expedition-config)
  plateau-channel
  nasa-hq-channel)

(println "Rovers started up")

(a/<!! (a/timeout 100000))
(println "End")


