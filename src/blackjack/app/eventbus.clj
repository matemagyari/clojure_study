(ns blackjack.app.eventbus
  (:require [clojure.core.async :refer [go go-loop chan close! <! <!! >! >!!
                                        timeout filter< put! take! pub sub unsub unsub-all
                                        thread]]))


(def event-channel (chan 100))

(defn- append-subscriber! [publication topic handler-fn]
  (let [sub-chan (chan)]
    (sub publication topic sub-chan)
    (go-loop []
      (when-let [event (<! sub-chan)]
        ;(println (str "event: " (:type event)))
        (handler-fn event))
      (recur))))

(defn init-event-handlers! [event-handlers]
  (let [publication (pub event-channel :type)]
    (doseq [handler event-handlers
            :let [handler-fn (:do-fn handler)]]
      (doseq [type (:types handler)]
        (append-subscriber! publication type handler-fn)))))


(defn publish-event! [event]
  "Publishes an event to the event bus"
  (go (>! event-channel event)))


(defn publish-events! [events]
  "Publishes events to the event bus"
  (doseq [event events]
    (publish-event! event)))

(defn reset-event-bus! []
  "Resets the bus"
  (println "Do something here"))



