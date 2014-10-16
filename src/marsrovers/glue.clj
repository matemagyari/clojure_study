(ns ^{:author mate.magyari
      :doc "The glue exposes messaging and component abstractions. It hides core.async from the rest of the application."}
  marsrovers.glue
  (:require [clojure.core.async :as a]))

;; -----------------  private functions ------------------------

(declare send-msg!)

(defn- process-result!
  "state - reseting the atom to this state
   msgs - messages to send
   effects - effects to execute. These are functions with deliberate side-effect and nil return value"
  [entity-atom {state :state
                msgs :msgs
                effects :effects}]
  (when state
    (reset! entity-atom state))
  (when effects
    (doseq [e! effects] (e!)))
  (when msgs
    (doseq [msg msgs] (send-msg! msg))))

;; -----------------  private functions ------------------------

(defn chan
  "Creates a channel. The rest of the applicatio doesn't have to know that it is a core.async channel."
  ([] (a/chan 1000))
  ([buffer] (a/chan buffer)))

(defn start-component!
  "Starts up a component. A component is composed of:
   entity-atom - the atom holding the value of the component
   msg-processing-fn - a function describing the component's behaviour"
  [entity-atom msg-processing-fn]
  (a/go-loop []
    (when-let [in-msg (a/<! (:in-channel @entity-atom))]
      (let [result (msg-processing-fn in-msg)]
        (process-result! entity-atom result)))
    (recur)))


(defn start-simple-component!
  "Starts up a simple component. A simple component is composed of:
   in-channel - the channel the component listens to
   msg-processing-fn - a function describing the component's behaviour"
  [in-channel msg-processing-fn!]
  (a/go-loop []
    (when-let [in-msg (a/<! in-channel)]
      (msg-processing-fn! in-msg))
    (recur)))

(defn send-msg! [{:keys [body target delay] :as msg}]
  "Sends a message
   body - the message content
   target - the channel to send
   delay - delay in milliseconds, optional"
  {:pre [(some? target) (some? body)]}
  (a/go
    (when delay
      (a/<! (a/timeout (* delay 10))))
    (try
      (a/>! target body)
      (catch AssertionError err
        (println (str "AssertionError on message" msg (.getMessage err)))))))

(defn send-msg!-old [{:keys [body target delay] :as msg}]
  {:pre [(some? target) (some? body)]}
  (when delay
    (a/<!! (a/timeout (* delay 10))))
  (try
    (a/>!! target body)
    (catch AssertionError err
      (println (str "AssertionError on message" msg (.getMessage err))))))