(ns blackjack.domain.game.end-to-end-test
  (:require [blackjack.app.eventbus :as e]
            [blackjack.app.eventhandlers :as eh]
            [blackjack.util.shared :as s]
            [blackjack.config.registry :as r]
            [blackjack.app.service.game-app-service :as gs]
            [blackjack.app.service.seating-app-service :as ss]
            [blackjack.app.service.registration-app-service :as rs]
            [blackjack.domain.game.game-test :as gt]
            [blackjack.domain.game.game :as g]
            [blackjack.domain.game.game-repository :as gr]
            [blackjack.domain.player.player :as p]
            [blackjack.domain.player.player-repository :as pr]
            [blackjack.domain.table.table :as t]
            [blackjack.domain.table.table-repository :as tr]
            [blackjack.domain.cashier.wallet-service :as w]
            [blackjack.app.external-event-publisher :as eep]
            [blackjack.domain.game.fakes :as f]
            [clojure.test :as test]
            ))



;;test agent
(defprotocol TestAgent
  (register! [this player-name])
  (seat-player! [this player-id table-id])
  (assert-event-sent! [this event]))

(defrecord AppLevelTestAgent []
  TestAgent
  (register! [this player-name]
    (rs/register! player-name))
  (seat-player! [this player-id table-id]
    (ss/seat-player! player-id table-id))
  (assert-event-sent! [this event]
    assert-event-sent! #(test/is (f/event-sent? r/external-event-bus event))))

;;cleanup
(e/reset-event-bus!)
(pr/clear! r/player-repository)
(tr/clear! r/table-repository)
(gr/clear! r/game-repository)
(w/clear! r/wallet-service)

(def table (t/create-new-table))
(tr/save-table! r/table-repository table)

(defn equals [x y]
  (test/is (= x y)))

(test/deftest a-game-test
  (with-redefs [g/new-deck (fn [] [[:club :2]
                                   [:spade :2]
                                   [:diamond :7]
                                   [:heart :J]
                                   [:diamond :5]
                                   [:heart :3]
                                   [:diamond :4]
                                   [:heart :2]])
                r/external-event-bus f/fake-ext-event-bus]
    (let [table-id (:id table)
          player-name "Paul"
          dealer-name "Dean"
          test-agent (->AppLevelTestAgent)
          player-id (register! test-agent player-name)
          dealer-id (register! test-agent dealer-name)
          get-game-fn #(first (gr/get-games r/game-repository))
          get-balance #(w/get-balance r/wallet-service %)
          flush-events! #(e/flush-events-with! (eh/event-handlers))]
      (equals 20000 (get-balance player-id))
      (equals 20000 (get-balance dealer-id))
      (seat-player! test-agent dealer-id table-id)
      (flush-events!)
      (assert-event-sent! test-agent {:event {:table-id table-id
                                              :players [dealer-id]
                                              :type :table-seating-changed-event}
                                      :addressee {:table-id table-id}})
      (seat-player! test-agent player-id table-id)
      (flush-events!)
      (assert-event-sent! test-agent {:event {:table-id table-id
                                              :players [dealer-id player-id]
                                              :type :table-seating-changed-event}
                                      :addressee {:table-id table-id}})
      (let [game (get-game-fn)
            game-id (:id game)
            do-action! (fn [p a] ((gs/handle-action! {:game-id game-id :player-id p :type a})
                                   (flush-events!)))]
        (assert-event-sent! test-agent {:event {:game-id game-id
                                                :table-id table-id
                                                :type :game-started-event}
                                        :addressee {:table-id table-id}})
        (do-action! player-id :hit)
        (do-action! dealer-id :hit)
        (do-action! player-id :hit)
        (do-action! dealer-id :hit)
        (assert-event-sent! test-agent {:event {:game-id game-id
                                                :table-id table-id
                                                :player-id dealer-id
                                                :type :public-card-dealt-event}
                                        :addressee {:table-id table-id}})
        (assert-event-sent! test-agent {:event {:game-id game-id
                                                :table-id table-id
                                                :player-id dealer-id
                                                :card [:heart :2]
                                                :type :private-card-dealt-event}
                                        :addressee {:table-id table-id
                                                    :player-id dealer-id}})
        (do-action! player-id :stand)
        (assert-event-sent! test-agent {:event {:game-id game-id
                                                :table-id table-id
                                                :player-id player-id
                                                :type :player-stands-event}
                                        :addressee {:table-id table-id}})
        (do-action! dealer-id :stand)
        (assert-event-sent! test-agent {:event {:game-id game-id
                                                :table-id table-id
                                                :winner player-id
                                                :type :game-finished-event}
                                        :addressee {:table-id table-id}})
        )
      ;;assertions
      (let [game (get-game-fn)
            player (pr/get-player r/player-repository player-id)
            table (tr/get-table r/table-repository table-id)]
        (equals :finished (:state game))
        (equals player-id (g/winner-of game))
        (equals 20500 (get-balance player-id))
        (equals 19500 (get-balance dealer-id))
        (equals 1 (:win-number player))
        (empty? (:players table)))
      )))

(test/run-tests)

(println "Yup Finished")
