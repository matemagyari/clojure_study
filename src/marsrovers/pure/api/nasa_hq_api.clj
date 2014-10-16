(ns
  ^{:author mate.magyari
    :doc "NASA HQ component's API description"}
  marsrovers.pure.api.nasa-hq-api)

(defn start-expedition-msg [configs]
  {:pre [(some? configs)]}
  {:rover-configs configs :type :start-expedition})

(defn register-rover-msg [rover-id rover-config rover-channel]
  {:pre [(every? some? [rover-id rover-channel])]}
  {:rover-id rover-id :rover-channel rover-channel :rover-config rover-config :type :register-rover})

(defn rover-registered-msg [] {:type :rover-registered})


