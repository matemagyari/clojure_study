(ns
  ^{:author mate.magyari}
  marsrovers.app.nasa-hq
  (:require [marsrovers.pure.nasa-hq :as n]
            [marsrovers.util :as u]))

(defn start-nasa-hq! [hq-atom]
  (u/start-component!
    hq-atom
    (:in-channel @hq-atom)
    (fn [in-msg]
      (n/receive @hq-atom in-msg))))
