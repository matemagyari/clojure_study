(ns
  ^{:author mate.magyari
    :doc "Rover components' API description"}
  marsrovers.pure.api.rover-api)

(defn position-msg [rover-id rover-position rover-channel]
  {:pre [(some? rover-id) (some? rover-position) (some? rover-channel)]}
  {:rover-id rover-id :rover-position rover-position :rover-channel rover-channel :type :position})

(defn deploy-rover-msg [rover-position controller-channel]
  {:pre [(some? rover-position) (some? controller-channel)]}
  {:rover-position rover-position :controller-channel controller-channel :type :deploy-rover})

(defn tick-msg [rover-id] {:type :tick :id rover-id})




