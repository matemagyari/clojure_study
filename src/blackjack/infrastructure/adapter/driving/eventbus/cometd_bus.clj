(ns blackjack.infrastructure.adapter.driving.eventbus.cometd-bus
  (:use blackjack.app.eventbus))

(defrecord CometDEventBus []
  ExternalEventBus
  (publish! [this event]
    ;(println "External bus" event)
    nil
    ))