(ns mini-projects.blackjack.port.wallet-service)

(defprotocol WalletService
  (get-balance [this player])
  (clear! [this])
  (credit! [this player amount])
  (debit! [this player amount])
  (create-account! [this player start-balance]))