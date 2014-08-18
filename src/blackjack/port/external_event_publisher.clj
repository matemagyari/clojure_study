(ns blackjack.port.external-event-publisher)

(defn to-private-external-event [event]
  {:event event
   :addressee {:table-id (:table-id event)
               :player-id (:player-id event)}})

(defn to-public-external-event [event]
  {:event event
   :addressee {:table-id (:table-id event)}})

(defprotocol ExternalEventBus
  (publish! [this event]))