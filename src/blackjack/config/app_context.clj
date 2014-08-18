(ns
  ^{:author mate.magyari}
  blackjack.config.app-context
  (:require [blackjack.app.eventbus :as e]
            [blackjack.app.eventhandlers :as eh]))

(defn start []
  (e/init-event-handlers! (eh/event-handlers)))
