(ns
  ^{:author mate.magyari}
  marsrovers.api.nasa-hq-api
  )

(defn start-expedition-msg [configs]
  {:pre [(some? configs)]}
  {:rover-configs configs :type :start-expedition})

(defn register-rover-msg [rover-id rover-channel]
  {:pre [(some? rover-id) (some? rover-channel)]}
  {:rover-id rover-id :rover-channel rover-channel :type :register-rover})

(defn rover-registered-msg [] {:type :rover-registered})


