(ns
  ^{:author mate.magyari}
  marsrovers.app.rover-controller
  (:require [marsrovers.pure.rover-controller :as r]
            [marsrovers.util :as u]))

(defn start-controller! [controller-atom]
  (u/start-component!
    controller-atom
    (:in-channel @controller-atom)
    (fn [in-msg]
      (r/receive @controller-atom in-msg))))