(ns
  ^{:author mate.magyari}
  blackjack.domain.game.lock-test
  (:require [blackjack.infrastructure.adapter.driving.gamerepository.in-memory :refer :all]
            [blackjack.port.game-repository :as gr]
            [blackjack.app.lockable :as lo]))

(def repo (->InMemoryGameRepository))

;(lo/acquire-lock! repo 1)
(lo/release-lock! repo 1)

(lo/with-lock 1 repo
  (let [x (inc 3)]
    (println "I am inside a lock")
    x))


