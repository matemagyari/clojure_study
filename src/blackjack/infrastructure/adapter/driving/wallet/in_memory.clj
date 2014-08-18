(ns blackjack.infrastructure.adapter.driving.wallet.in-memory
  (:require [blackjack.port.wallet-service :refer :all]
        [blackjack.app.lockable :refer :all]
        [blackjack.infrastructure.adapter.driving.shared.locking :as lo]))

(def ^:private wallet-map (ref {}))
(def ^:private locks (ref {}))

(defn- execute [op player-id amount]
  (dosync
    (alter wallet-map update-in [player-id] op amount)))

(defrecord InMemoryWallet []
  WalletService
  (get-balance [this player-id]
    (get @wallet-map player-id))
  (clear! [this] (dosync
                   (ref-set wallet-map {})))
  (credit! [this player-id amount] (execute + player-id amount))
  (debit! [this player-id amount] (execute - player-id amount))
  (create-account! [this player-id start-balance]
    (dosync
      (alter wallet-map assoc player-id start-balance)))
  Lockable
  (acquire-lock! [this id]
    (lo/acquire-lock! locks id))
  (release-lock! [this id]
    (lo/release-lock! locks id)))