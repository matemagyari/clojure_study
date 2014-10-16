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

(defn- create-controller [hq in-msg]
  (atom
    (c/controller (:rover-id in-msg) (:rover-channel in-msg) (:rover-config in-msg) (glue/chan) (:in-channel hq))))

;; -----------------  public functions ------------------------

(defn receive [hq in-msg start-controller-fn!]
  ;(u/log! "NASA HQ received msg " in-msg)
  (condp = (:type in-msg)

    :disaster (do
                (u/log! "Disaster happened")
                {:state (assoc hq :disaster true)})

    :register-rover (let [controller (create-controller hq in-msg)
                          msgs [(u/msg (:rover-channel in-msg) (hq/rover-registered-msg))
                                (u/msg (:in-channel @controller) (ca/start-rover-msg))]]
                      {:effects [#(start-controller-fn! controller)]
                       :msgs msgs
                       :state hq})
    (do
      (u/log! "Unknown msg in Nasa HQ " in-msg)
      {:state hq})))

(defn nasa-hq [expedition-config in-channel]
  {:disaster false
   :in-channel in-channel
   :expedition-config expedition-config
   :plateau-config (:plateau-config expedition-config)})