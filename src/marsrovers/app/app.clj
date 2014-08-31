(ns
  ^{:author mate.magyari}
  marsrovers.app.app
  (:require [marsrovers.pure.nasa-hq :as n]
            [marsrovers.pure.plateau :as p]
            [marsrovers.pure.rover :as r]
            [marsrovers.pure.rover-controller :as c]
            [marsrovers.util :as u]))

(defn start-nasa-hq! [hq-atom]
  (u/start-component!
    hq-atom
    (:in-channel @hq-atom)
    (fn [in-msg]
      (n/receive @hq-atom in-msg))))

(defn start-plateau! [plateau-atom]
  (u/start-component!
    plateau-atom
    (:in-channel @plateau-atom)
    (fn [in-msg]
      (p/receive @plateau-atom in-msg))))

(defn start-controller! [controller-atom]
  (u/start-component!
    controller-atom
    (:in-channel @controller-atom)
    (fn [in-msg]
      (c/receive @controller-atom in-msg))))

(defn start-rover! [rover-atom plateau-channel mediator-channel]
  (let [in-channel (:in-channel @rover-atom)]
    (u/start-component!
      rover-atom
      in-channel
      (fn [in-msg]
        (r/receive @rover-atom in-msg plateau-channel mediator-channel)))
    (u/send-msg! (u/msg in-channel r/msg-tick))))
