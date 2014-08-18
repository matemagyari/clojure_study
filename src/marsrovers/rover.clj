(ns
  ^{:author mate.magyari}
  marsrovers.rover
  (:require [clojure.core.async :as a :refer [go go-loop chan close! <! <!! >! >!!
                                        timeout filter< put! take! pub sub unsub unsub-all
                                        thread alts! alts!!]]))


;;should each actor have its own channel or one channel from mediator to actors and each actor pipes its own using transducers?
(defn rover [id channel]
  {:id id
   :state (atom {})
   :chan channel})


