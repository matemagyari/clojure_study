(ns blackjack.domain.cashier.wallet-service)

(defprotocol WalletService
  (credit! [this player amount])
  (debit! [this player amount])
  (create-account! [this player start-balance]))