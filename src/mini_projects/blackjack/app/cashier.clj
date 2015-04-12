(ns mini-projects.blackjack.app.cashier
  (:require [mini-projects.blackjack.port.wallet-service :as w]
            [mini-projects.blackjack.config.registry :as r]))

(def entry-fee 500)
(def starting-balance 20000)

(defn debit-entry-fee! [player]
  (w/debit! r/wallet-service player entry-fee))

(defn give-win! [player]
  (w/credit! r/wallet-service player (* 2 entry-fee)))

(defn create-account! [player]
  (w/create-account! r/wallet-service player starting-balance))
