(ns
  ^{:author mate.magyari}
  marsrovers.mars-expedition
  (:require [clojure.core.async :as a]
            [marsrovers.app.app :as app]
            [marsrovers.glue :as glue]
            [marsrovers.expedition-config-reader :as ecr]))

(def plateau-channel (glue/chan))
(def nasa-hq-channel (glue/chan))
(def expedition-config (ecr/expedition-config))

(app/start-world! expedition-config plateau-channel nasa-hq-channel)

(app/start-rovers!
  (count (:rover-configs expedition-config))
  plateau-channel
  nasa-hq-channel)


(a/<!! (a/timeout 1000))


