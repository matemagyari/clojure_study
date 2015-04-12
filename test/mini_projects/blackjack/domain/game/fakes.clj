(ns mini-projects.blackjack.domain.game.fakes
  (:require [mini-projects.blackjack.port.external-event-publisher :as eep]
            [mini-projects.blackjack.util.shared :as s]))


(def events (ref []))

(defprotocol FakeBus
  (event-sent? [this event]))

(defrecord FakeExternalEventBus []
  eep/ExternalEventBus
  (publish! [this event]
    ;(println "Fake bus" event)
    (dosync
      (alter events conj event)))
  FakeBus
  (event-sent? [this event]
    (s/seq-contains? @events event)))
;;todo remove
(def fake-ext-event-bus (->FakeExternalEventBus))

