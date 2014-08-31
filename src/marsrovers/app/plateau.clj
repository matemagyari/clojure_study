(ns
  ^{:author mate.magyari}
  marsrovers.app.plateau
  (:require [marsrovers.api.rover-api :as ra]
            [marsrovers.api.plateau-api :as pa]
            [marsrovers.pure.plateau :as p]
            [marsrovers.util :as u]))

(defn start-plateau! [plateau-atom]
  (u/start-component!
    plateau-atom
    (:in-channel @plateau-atom)
    (fn [in-msg]
      (p/receive @plateau-atom in-msg))))