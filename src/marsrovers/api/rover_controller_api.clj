(ns
  ^{:author mate.magyari}
  marsrovers.api.rover-controller-api
  (:require [marsrovers.util :as u])
  )

(defn start-rover-msg [] {:type :start-rover})
(defn rover-deployed-msg [] {:type :rover-deployed})

(defn rover-action-msg [action]
  {:pre [(u/valid-action? action)]}
  {:type :rover-action :action action})

(defn disaster-msg [rover-id rover-channel]
  {:pre [(some? rover-id) (some? rover-channel)]}
  {:type :disaster :rover-id rover-id :rover-channel rover-channel})

(defn posion-pill-msg [] {:type :poison-pill})
