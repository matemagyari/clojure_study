(ns
  ^{:author mate.magyari}
  mini-projects.blackjack.config.app-context
  (:require [mini-projects.blackjack.app.eventbus :as e]
            [mini-projects.blackjack.app.eventhandlers :as eh]))

(defn start []
  (e/init-event-handlers! (eh/event-handlers)))
