(ns
  ^{:author mate.magyari}
  marsrovers.app
  (:require [marsrovers.pure.nasa-hq :as n]
            [marsrovers.pure.plateau :as p]
            [marsrovers.pure.rover :as r]
            [marsrovers.pure.rover-controller :as c]
            [marsrovers.pure.util :as u]
            [marsrovers.glue :as glue]
            [marsrovers.display :as d]))

(defn- start-controller! [controller-atom]
  (glue/start-component!
    controller-atom
    (fn [in-msg]
      (c/receive @controller-atom in-msg))))

(defn- start-nasa-hq! [hq-atom]
  (glue/start-component!
    hq-atom
    (fn [in-msg]
      (n/receive @hq-atom in-msg start-controller!))))

(defn- start-plateau! [plateau-atom]
  (glue/start-component!
    plateau-atom
    (fn [in-msg]
      (p/receive @plateau-atom in-msg))))

(defn- start-displayer! [displayer-channel plateau-config dim-screen]
  (let [repaint! (d/repaint-fn [(:x plateau-config) (:y plateau-config)] dim-screen)
        count (atom 0)
        counter! (fn [msg]
                   (do (swap! count inc)
                     (println (str "Count" @count))))]
    (glue/start-simple-component! displayer-channel repaint!)))

(defn start-world! [expedition-config plateau-channel nasa-hq-channel displayer-channel dim-screen]
  (let [plateau-config (:plateau-config expedition-config)
        plateau-atom (atom
                       (p/plateau plateau-config plateau-channel displayer-channel))
        nasa-hq-atom (atom
                       (n/nasa-hq expedition-config nasa-hq-channel))]
    (start-nasa-hq! nasa-hq-atom)
    (start-plateau! plateau-atom)
    (start-displayer! displayer-channel plateau-config dim-screen)))

(defn start-rover! [rover-atom plateau-channel mediator-channel]
  (glue/start-component!
    rover-atom
    (fn [in-msg]
      (r/receive @rover-atom in-msg plateau-channel mediator-channel)))
  (glue/send-msg! (u/msg (:in-channel @rover-atom) r/msg-tick)))

(defn start-rovers! [n plateau-channel mediator-channel]
  (let [rover-atoms (for [i (range n)]
                      (atom
                        (r/rover i (glue/chan))))]
    (doseq [rover-atom rover-atoms]
      (start-rover! rover-atom plateau-channel mediator-channel))))