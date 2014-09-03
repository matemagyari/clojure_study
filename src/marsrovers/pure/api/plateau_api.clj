(ns
  ^{:author mate.magyari}
  marsrovers.pure.api.plateau-api)

(defn collision-msg [] {:type :collision})
(defn ack-msg [] {:type :ack})
(defn got-lost-msg [] {:type :got-lost})




