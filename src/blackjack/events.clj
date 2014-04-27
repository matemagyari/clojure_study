(ns blackjack.events)

(def event-buffer (ref []))

(defn publish-event [event]
  "Publishes an event to the event bus"
  (println event)
  (dosync
    (alter event-buffer conj event)))

(defn flush-events-with [event-handlers]
  (dosync
    (while (not-empty @event-buffer)
      (let [event (first @event-buffer)
            match-with? (fn [handler] ((handler :match-fn) event))
            handle-event-with (fn [handler] ((handler :do-fn) event))]
        (doseq [event-handler event-handlers
             :when (match-with? event-handler)]
          (handle-event-with event-handler))
        (alter event-buffer rest)))))

;;(blackjack.events/flush-events-with (blackjack.eventhandlers/event-handlers))