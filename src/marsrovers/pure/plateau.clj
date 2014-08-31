(ns
  ^{:author mate.magyari}
  marsrovers.pure.plateau
  (:require [marsrovers.api.rover-api :as ra]
            [marsrovers.api.plateau-api :as pa]
            [marsrovers.util :as u]))

(defn- is-rover-lost [rover-position plateau-config]
  (let [x (:x rover-position)
        y (:y rover-position)]
    (or
      (neg? x) (> x (:x plateau-config))
      (neg? y) (> y (:y plateau-config)))))

(defn- collisions-msgs [plateau rover-id rover-position]
  (for [rover (-> plateau :rover-positions vals)
        :when (and
                (= rover-position (:rover-position rover))
                (not= rover-id (:rover-id rover)))]
    (u/msg
      (:rover-channel rover)
      (pa/collision-msg))))

(defn receive [plateau in-msg]
  (condp = (:type in-msg)

    :position (let [rover-id (:rover-id in-msg)
                    rover-channel (:rover-channel in-msg)
                    rover-position (:rover-position in-msg)
                    plateau (update-in plateau [:rover-positions]
                              conj [rover-id in-msg])]
                {:state plateau
                 :msgs (let [coll-msgs (collisions-msgs plateau rover-id rover-position)]
                         (cond
                           (not (empty? coll-msgs)) coll-msgs
                           (is-rover-lost
                             rover-position
                             (:config plateau)) [(u/msg rover-channel (pa/got-lost-msg))]
                           :else [(u/msg rover-channel (pa/ack-msg))])
                         )})

    ;;default
    {:state plateau}))

(defn plateau [config]
  {:rover-positions {}
   :config config
   :in-channel (u/chan)})
