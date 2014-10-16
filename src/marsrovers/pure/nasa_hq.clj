(ns
  ^{:author mate.magyari
    :doc "Pure functions describing the behaviour of the NASA HQ component"}
  marsrovers.pure.nasa-hq
  (:require [marsrovers.pure.api.nasa-hq-api :as hq]
            [marsrovers.pure.rover-controller :as c]
            [marsrovers.pure.api.rover-controller-api :as ca]
            [marsrovers.pure.util :as u]
            [marsrovers.glue :as glue]))

;; -----------------  private functions ------------------------

(defn- rover-configs [hq]
  (get-in hq [:expedition-config :rover-configs]))

(defn- all-rovers-registered? [hq]
  (=
    (count (:rovers hq))
    (count (rover-configs hq))))

(defn- create-controller [rover-id rover-channel rover-config hq-channel]
  (atom
    (c/controller rover-id rover-channel rover-config (glue/chan) hq-channel)))

(defn- create-controllers [hq]
  (let [pairs (map vector
                (:rovers hq)
                (rover-configs hq))]
    (for [[rover rover-config] pairs]
      (create-controller (:rover-id rover) (:rover-channel rover) rover-config (:in-channel hq)))))

(defn- register-rover2 [hq in-msg]
  (create-controller (:rover-id in-msg) (:rover-channel in-msg) (:rover-config in-msg) (:in-channel hq)))

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

;; -----------------  public functions ------------------------

(defn receive [hq in-msg start-controller-fn!]
  ;(u/log! "NASA HQ received msg " in-msg)
  (condp = (:type in-msg)

    :disaster (do
                (u/log! "Disaster happened")
                {:state (assoc hq :disaster true)})

    :register-rover (let [controller (register-rover2 hq in-msg)
                          msgs [(u/msg (:rover-channel in-msg) (hq/rover-registered-msg))
                                (u/msg (:in-channel @controller) (ca/start-rover-msg))]]
                      {:effects [#(start-controller-fn! controller)]
                       :msgs msgs
                       :state hq})

    :register-rover2 (let [hq (register-rover hq in-msg)
                           msgs (conj (start-rover-msgs hq)
                                  (u/msg (:rover-channel in-msg) (hq/rover-registered-msg)))]
                       {:effects [#(doseq [co (:controllers hq)]
                                     (start-controller-fn! co))]
                        :msgs msgs
                        :state hq})
    (do
      (u/log! "Unknown msg in Nasa HQ " in-msg)
      {:state hq})))

(defn nasa-hq [expedition-config in-channel]
  {:disaster false
   :in-channel in-channel
   :expedition-config expedition-config
   :plateau-config (:plateau-config expedition-config)
   :rovers []})