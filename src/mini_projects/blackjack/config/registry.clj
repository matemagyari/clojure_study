(ns mini-projects.blackjack.config.registry
  (:require [mini-projects.blackjack.util.shared :as shared]
            [mini-projects.blackjack.infrastructure.adapter.driving.gamerepository.in-memory :refer (->InMemoryGameRepository)]
            [mini-projects.blackjack.infrastructure.adapter.driving.playerrepository.in-memory :refer (->InMemoryPlayerRepository)]
            [mini-projects.blackjack.infrastructure.adapter.driving.tablerepository.in-memory :refer (->InMemoryTableRepository)]
            [mini-projects.blackjack.infrastructure.adapter.driving.wallet.in-memory :refer (->InMemoryWallet)]
            [mini-projects.blackjack.infrastructure.adapter.driving.eventbus.cometd-bus :refer (->CometDEventBus)]
            [clojure.java.io :as io :only [resource reader]]
            [clojure.edn :as edn :only [read-string]]))

(def properties (->> "mini-projects.blackjack.edn" io/resource io/reader slurp edn/read-string))

(def persistence-type (:mini-projects.blackjack.persistence.type properties))

(def player-repository (condp = persistence-type
                         :memory (->InMemoryPlayerRepository)
                         :mongo (->InMemoryPlayerRepository)))
(def table-repository (condp = persistence-type
                        :memory (->InMemoryTableRepository)
                        :mongo (->InMemoryTableRepository)))
(def game-repository (condp = persistence-type
                       :memory (->InMemoryGameRepository)
                       :mongo (->InMemoryGameRepository)))
(def external-event-bus (->CometDEventBus))
(def wallet-service (->InMemoryWallet))

