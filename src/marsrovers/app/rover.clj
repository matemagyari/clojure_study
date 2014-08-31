(ns
  ^{:author mate.magyari}
  marsrovers.app.rover
  (:require [clojure.core.async :as a]
            [marsrovers.util :as u]
            [marsrovers.pure.rover :as r]
            ))

(defn start-rover! [rover-atom plateau-channel mediator-channel]
  (let [in-channel (:in-channel @rover-atom)]
    (u/start-component!
      rover-atom
      in-channel
      (fn [in-msg]
        (r/receive @rover-atom in-msg plateau-channel mediator-channel)))
    (u/send-msg! (u/msg in-channel r/msg-tick))))
