(ns
  ^{:author mate.magyari}
  marsrovers.glue
  (:require [clojure.core.async :as a]))

(defn send-msg!-old [msg]
  {:pre [(some? (:target msg)) (some? (:body msg))]}
  (a/go
    (if-let [delay (:delay msg)]
      (a/<! (a/timeout (* delay 10))))
    (try
      (a/>! (:target msg) (:body msg))
      (catch AssertionError err
        (println (str "AssertionError on message" msg (.getMessage err)))))))


(defn send-msg! [msg]
  {:pre [(some? (:target msg)) (some? (:body msg))]}
  (if-let [delay (:delay msg)]
    (a/<!! (a/timeout (* delay 10))))
  (try
    (a/>!! (:target msg) (:body msg))
    (catch AssertionError err
      (println (str "AssertionError on message" msg (.getMessage err))))))

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

(defn chan
  ([] (a/chan 100))
  ([buffer] (a/chan buffer)))

(defn start-component! [entity-atom msg-processing-fn]
  (a/go-loop []
    (when-let [in-msg (a/<! (:in-channel @entity-atom))]
      (let [result (msg-processing-fn in-msg)]
        (process-result! entity-atom result)))
    (recur)))


(defn start-simple-component! [in-channel msg-processing-fn!]
  (a/go-loop []
    (when-let [in-msg (a/<! in-channel)]
      (msg-processing-fn! in-msg))
    (recur)))