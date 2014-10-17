(ns
  ^{:author mate.magyari
    :doc "Pure functions describing the behaviour of the Rover Controller component"}
  marsrovers.pure.rover-controller
  (:require [marsrovers.pure.api.rover-api :as r]
            [marsrovers.pure.api.rover-controller-api :as c]
            [marsrovers.pure.util :as u]))

(defn- rover-position [controller]
  {:post [(some? %)]}
  (get-in controller [:rover-config :position]))

(defn- rover-channel [controller]
  (get-in controller [:rover :rover-channel]))

(defn- rover-msg [controller body]
  (u/msg (rover-channel controller) body))

(defn- deploy-rover-msg [controller]
  (rover-msg
    controller
    (r/deploy-rover-msg (rover-position controller) (:in-channel controller))))

(defn- rover-action-msg [controller]
  (rover-msg
    controller
    (c/rover-action-msg (-> controller :actions peek))))

(defn- pop-action [controller]
  (let [action (->> controller :actions (take 1) first)
        controller (update-in controller [:actions] #(drop 1 %))]
    [action controller]))

(defn- has-actions? [controller]
  (-> (:actions controller) empty? not))

(defn- poison-pill-msg [controller]
  (rover-msg
    controller
    (c/posion-pill-msg)))

(defn- result [state & msgs]
  {:pre [(some? state) (some? msgs)]}
  {:state state :msgs msgs})

(defn- controller-log! [controller & text]
  (u/log! "Controller " (get-in controller [:rover :rover-id]) ": " text))

(defn- action-result [controller]
  (let [[action controller] (pop-action controller)
        msg-body (c/rover-action-msg action)
        msg (rover-msg controller msg-body)]
    (result controller msg)))

(defn receive [controller in-msg]
  ;(controller-log! controller " Message arrived: " (:rover-position in-msg))
  (condp = (:type in-msg)
    :start-rover (result
                   controller
                   (deploy-rover-msg controller))
    :rover-deployed (action-result controller)
    :position (if (has-actions? controller)
                (action-result controller)
                (result
                  controller
                  (poison-pill-msg controller)))))

(defn controller [rover-id rover-channel rover-config in-channel hq-channel]
  {:pre [(every? some? [rover-id rover-channel rover-config in-channel hq-channel])]}
  {:rover {:rover-id rover-id :rover-channel rover-channel}
   :actions (:actions rover-config)
   :rover-config rover-config
   :in-channel in-channel
   :hq-channel hq-channel})
