(ns blackjack.config.registry
  (:use [blackjack.util.shared :as shared]
            [blackjack.infrastructure.adapter.driving.gamerepository.in-memory :as g]
            [blackjack.infrastructure.adapter.driving.playerrepository.in-memory :as p]
            [blackjack.infrastructure.adapter.driving.tablerepository.in-memory :as t]
            [blackjack.infrastructure.adapter.driving.eventbus.cometd-bus :as e]
            [blackjack.infrastructure.adapter.driving.wallet.in-memory :as w]))

(def player-repository (->InMemoryPlayerRepository))
(def table-repository (->InMemoryTableRepository))
(def game-repository (->InMemoryGameRepository))
(def external-event-bus (->CometDEventBus))
(def wallet-service (->InMemoryWallet))

