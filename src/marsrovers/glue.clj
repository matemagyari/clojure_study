(ns
  ^{:author mate.magyari}
  marsrovers.glue
  (:require [clojure.core.async :as a]))

(defn send-msg! [msg]
  {:pre [(some? (:target msg)) (some? (:body msg))]}
  (a/go
    (if-let [delay (:delay msg)]
      (a/<! (a/timeout (* delay 10))))
    (a/>! (:target msg) (:body msg))))

(defn- send-msgs! [msgs]
  (doseq [msg msgs] (send-msg! msg)))

(defn- process-result! [entity-atom {state :state
                                     msgs :msgs
                                     effects :effects}]
  (when state
    (reset! entity-atom state))
  (when effects
    (doseq [e! effects] (e!)))
  (when msgs
    (send-msgs! msgs)))

(defn chan []
  (a/chan 100))

(defn start-component! [entity-atom msg-processing-fn]
  (a/go-loop []
    (when-let [in-msg (a/<! (:in-channel @entity-atom))]
      (let [result (msg-processing-fn in-msg)]
        (process-result! entity-atom result)))
    (recur)))

