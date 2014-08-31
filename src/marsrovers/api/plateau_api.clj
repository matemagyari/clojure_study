(ns
  ^{:author mate.magyari}
  marsrovers.api.plateau-api
  )

(defn collision-msg [] {:type :collision})
(defn ack-msg [] {:type :ack})
(defn got-lost-msg [] {:type :got-lost})




