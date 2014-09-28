(ns
  ^{:author mate.magyari}
  marsrovers.mars-expedition
  (:require [clojure.core.async :as a]
            [marsrovers.app :as app]
            [marsrovers.glue :as glue]
            [marsrovers.expedition-config-reader :as ecr]))

(def displayer-channel (glue/chan))
(def plateau-channel (glue/chan))
(def nasa-hq-channel (glue/chan))
(def expedition-config (ecr/expedition-config))
(def dim-screen [600 600])

(app/start-world! expedition-config plateau-channel nasa-hq-channel displayer-channel dim-screen)

(app/start-rovers!
  (count (:rover-configs expedition-config))
  plateau-channel
  nasa-hq-channel)


(a/<!! (a/timeout 1000))


