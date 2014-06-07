(ns blackjack.infrastructure.adapter.driving.wallet.in-memory
  (:use [blackjack.domain.cashier.wallet-service]))

(def wallet-map (ref {}))

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
      (alter wallet-map assoc player-id start-balance))))