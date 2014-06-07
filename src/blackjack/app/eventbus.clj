(ns blackjack.app.eventbus)

(def event-buffer (ref []))

(defn publish-event! [event]
  "Publishes an event to the event bus"
  ;(println event)
  (dosync
    (alter event-buffer conj event)))

(defn publish-events! [events]
  "Publishes events to the event bus"
  (doseq [event events]
    (publish-event! event)))

(defn reset-event-bus! []
  "Resets the bus"
  (dosync
    (ref-set event-buffer [])))

(defn flush-events-with! [event-handlers]
  "Flushes event bus"
  (let [events @event-buffer]
    (reset-event-bus!)
    (doseq [event events
            handler event-handlers]
      (when ((handler :match-fn) event)
        (reset-event-bus!)
        ((handler :do-fn) event)
        (flush-events-with! event-handlers)))))
