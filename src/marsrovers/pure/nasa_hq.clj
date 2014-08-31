(ns
  ^{:author mate.magyari}
  marsrovers.pure.nasa-hq
  (:require [marsrovers.api.nasa-hq-api :as hq]
            [marsrovers.pure.rover-controller :as c]
            [marsrovers.app.rover-controller :as ac]
            [marsrovers.api.rover-controller-api :as ca]
            [marsrovers.util :as u]))

(defn- rover-configs [hq]
  (get-in hq [:expedition-config :rover-configs]))

(defn- all-rovers-registered? [hq]
  (=
    (count (:rovers hq))
    (count (rover-configs hq))))

(defn- create-controller [rover-id rover-channel rover-config hq-channel]
  (atom
    (c/controller rover-id rover-channel rover-config (u/chan) hq-channel)))

(defn- create-controllers [hq]
  (let [pairs (map vector
                (:rovers hq)
                (rover-configs hq))]
    (println pairs)
    (for [[rover rover-config] pairs]
      (create-controller (:rover-id rover) (:rover-channel rover) rover-config (:in-channel hq)))))

(defn- register-rover [hq in-msg]
  (let [rover (select-keys in-msg [:rover-id :rover-channel])
        hq (update-in hq [:rovers] conj rover)]
    (cond
      (all-rovers-registered? hq) (assoc hq :controllers (create-controllers hq))
      :else hq)))

(defn- start-rover-msgs [hq]
  (for [c (:controllers hq)
        :let [ch (:in-channel @c)]]
    (u/msg ch (ca/start-rover-msg))))

(defn- start-rovers-effect [hq]
  (for [c (:controllers hq)
        :let [ch (:in-channel @c)]]
    (u/msg ch (ca/start-rover-msg))))

(defn receive [hq in-msg]
  ;(u/log! "NASA HQ received msg " in-msg)
  (condp = (:type in-msg)

    :disaster (do
                (u/log! "Disaster happened")
                {:state (assoc hq :disaster true)})

    :register-rover (do
                      (let [hq (register-rover hq in-msg)
                            msgs (conj (start-rover-msgs hq)
                                   (u/msg (:rover-channel in-msg) (hq/rover-registered-msg)))]
                        {:effects [#(doseq [co (:controllers hq)]
                                      (ac/start-controller! co))]
                         :msgs msgs
                         :state hq}))
    (do
      (u/log! "Unknown msg in Nasa HQ " in-msg)
      {:state hq})))

(defn nasa-hq [expedition-config]
  {:disaster false
   :in-channel (u/chan)
   :expedition-config expedition-config
   :plateau-config (:plateau-config expedition-config)
   :rovers []})