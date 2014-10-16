(ns
  ^{:author mate.magyari
    :doc "Plateau component's API description"}
  marsrovers.pure.api.plateau-api)

(defn collision-msg [] {:type :collision})
(defn ack-msg [] {:type :ack})
(defn got-lost-msg [] {:type :got-lost})




