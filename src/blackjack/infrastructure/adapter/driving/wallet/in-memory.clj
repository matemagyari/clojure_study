(ns blackjack.infrastructure.adapter.driving.wallet.in-memory  
  (:use [blackjack.domain.cashier.wallet-service]))

(def wallet-map {})

(defn- execute [op player-id amount]
  (let [wallet (player-id wallet-map)]
      (dosync
        (ref-set wallet op amount))))

(defrecord InMemoryWallet []
  WalletService
  (credit! [this player-id amount] (execute + player-id amount))
  (debit! [this player-id amount] (execute - player-id amount))
  (create-account! [this player-id start-balance]
    (assoc wallet-map
           player-id 0)))