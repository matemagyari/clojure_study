(ns mini-projects.blackjack.infrastructure.adapter.driving.eventbus.cometd-bus
  (:use mini-projects.blackjack.port.external-event-publisher))

(defrecord CometDEventBus []
  ExternalEventBus
  (publish! [this event]
    (println "External bus" event)
    nil
    ))