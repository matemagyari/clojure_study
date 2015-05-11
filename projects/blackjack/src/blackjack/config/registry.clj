(ns blackjack.config.registry
  (:require [blackjack.util.shared :as shared]
            [blackjack.infrastructure.adapter.driving.gamerepository.in-memory :refer (->InMemoryGameRepository)]
            [blackjack.infrastructure.adapter.driving.playerrepository.in-memory :refer (->InMemoryPlayerRepository)]
            [blackjack.infrastructure.adapter.driving.tablerepository.in-memory :refer (->InMemoryTableRepository)]
            [blackjack.infrastructure.adapter.driving.wallet.in-memory :refer (->InMemoryWallet)]
            [blackjack.infrastructure.adapter.driving.eventbus.cometd-bus :refer (->CometDEventBus)]
            [clojure.java.io :as io :only [resource reader]]
            [clojure.edn :as edn :only [read-string]]))

(def properties (->> "blackjack.edn" io/resource io/reader slurp edn/read-string))

(def persistence-type (:blackjack.persistence.type properties))

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

