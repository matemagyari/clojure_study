(ns
  ^{:author mate.magyari
    :doc "Rover controller component's API description"}
  marsrovers.pure.api.rover-controller-api
  (:require [marsrovers.pure.util :as u]))

(defn start-rover-msg [] {:type :start-rover})
(defn rover-deployed-msg [] {:type :rover-deployed})

(defn rover-action-msg [action]
  {:pre [(u/valid-action? action)]}
  {:type :rover-action :action action})

(defn disaster-msg [rover-id rover-channel]
  {:pre [(every? some? [rover-id rover-channel])]}
  {:type :disaster :rover-id rover-id :rover-channel rover-channel})

(defn posion-pill-msg [] {:type :poison-pill})
