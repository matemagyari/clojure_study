(ns
  ^{:author mate.magyari
    :doc "The app is on the top of the code. It knows the domain, takes the pure functions descibing the behaviour
          of the components and wires them together with the glue"}
  marsrovers.app
  (:require [marsrovers.pure.nasa-hq :as n]
            [marsrovers.pure.plateau :as p]
            [marsrovers.pure.rover :as r]
            [marsrovers.pure.api.rover-api :as ra]
            [marsrovers.pure.rover-controller :as c]
            [marsrovers.pure.util :as u]
            [marsrovers.glue :as glue]
            [marsrovers.monitor :as monitor]
            [marsrovers.display :as d]))

;; -----------------  private functions ------------------------
(defn- start-controller!
  "Starts up a mars rover controller"
  [controller-atom]
  (glue/start-component!
    controller-atom
    (fn [in-msg]
      (c/receive @controller-atom in-msg))))

(defn- start-nasa-hq!
  "Starts up the NASA HQ"
  [hq-atom]
  (glue/start-component!
    hq-atom
    (fn [in-msg]
      (n/receive @hq-atom in-msg start-controller!))))

(defn- start-plateau!
  "Starts up the Plateau"
  [plateau-atom]
  (glue/start-component!
    plateau-atom
    (fn [in-msg]
      (p/receive @plateau-atom in-msg))))

(defn- start-displayer!
  "Starts up the displayer component - currently it's a SWING UI"
  [displayer-channel plateau-config dim-screen]
  (let [repaint! (d/repaint-fn [(:x plateau-config) (:y plateau-config)] dim-screen)
        sampler-repaint! (u/sampler-filter repaint! 10000)
        ;simple-logger! (u/sampler-filter #() 1000)
        do-nothing (fn [& args] nil)]
    (glue/start-simple-component! displayer-channel sampler-repaint!)))


;; -----------------  public functions ------------------------
(defn start-world!
  "Starts up the world the rovers will roam"
  [expedition-config plateau-channel nasa-hq-channel displayer-channel dim-screen]
  (let [plateau-config (:plateau-config expedition-config)
        plateau-atom (atom
                       (p/plateau plateau-config plateau-channel displayer-channel))
        nasa-hq-atom (atom
                       (n/nasa-hq expedition-config nasa-hq-channel))]
    (start-nasa-hq! nasa-hq-atom)
    (start-plateau! plateau-atom)
    (start-displayer! displayer-channel plateau-config dim-screen)))

(defn start-rover!
  "Starts up a single rover"
  [rover-atom plateau-channel mediator-channel]
  (glue/start-component!
    rover-atom
    (fn [in-msg]
      (r/receive @rover-atom in-msg plateau-channel mediator-channel)))
  (glue/send-msg! (u/msg (:in-channel @rover-atom) (ra/tick-msg (:id @rover-atom)))))


(defn start-rovers!
  "Starts up a bunch of rovers"
  [rover-configs plateau-channel mediator-channel]
  (let [rover-atoms (for [i (range (count rover-configs))]
                      (atom
                        (r/rover i (nth rover-configs i) (glue/chan))))
        rovers-monitor-watch (monitor/rovers-monitor)]
    (doseq [rover-atom rover-atoms]
      (add-watch rover-atom :all-rovers rovers-monitor-watch)
      (start-rover! rover-atom plateau-channel mediator-channel))))

