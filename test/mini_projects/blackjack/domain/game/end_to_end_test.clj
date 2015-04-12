(ns mini-projects.blackjack.domain.game.end-to-end-test
  (:require [mini-projects.blackjack.app.eventbus :as e]
            [mini-projects.blackjack.app.eventhandlers :as eh]
            [mini-projects.blackjack.util.shared :as s]
            [mini-projects.blackjack.config.app-context :as ac]
            [mini-projects.blackjack.config.registry :as r]
            [mini-projects.blackjack.app.service.game-app-service :as gs]
            [mini-projects.blackjack.app.service.seating-app-service :as ss]
            [mini-projects.blackjack.app.service.registration-app-service :as rs]
            [mini-projects.blackjack.domain.game.game :as g]
            [mini-projects.blackjack.domain.player.player :as p]
            [mini-projects.blackjack.port.game-repository :as gr]
            [mini-projects.blackjack.port.player-repository :as pr]
            [mini-projects.blackjack.port.table-repository :as tr]
            [mini-projects.blackjack.domain.table.table :as t]
            [mini-projects.blackjack.port.wallet-service :as w]
            [mini-projects.blackjack.port.external-event-publisher :as eep]
            [mini-projects.blackjack.domain.game.fakes :as f]
            [mini-projects.blackjack.domain.game.test-fixture :as tf]
            [clojure.test :as test]
            ))



;;test agent
(defprotocol TestAgent
  (register! [this player-name])
  (seat-player! [this player-id table-id])
  (do-action! [this game-id player-id action])
  (assert-event-sent! [this event]))

(defrecord AppLevelTestAgent []
  TestAgent
  (register! [this player-name]
    (rs/register! player-name))
  (seat-player! [this player-id table-id]
    (do
      (ss/seat-player! player-id table-id)
      (tf/sleep-a-bit)))
  (do-action! [this game-id player-id action]
    (do
      (gs/handle-action! {:game-id game-id :player-id player-id :type action})
      (tf/sleep-a-bit)))
  (assert-event-sent! [this event]
    (test/is (f/event-sent? r/external-event-bus event))))

;;cleanup
(defn- clean-up []
  (println "clean up")
  (e/reset-event-bus!)
  (pr/clear! r/player-repository)
  (tr/clear! r/table-repository)
  (gr/clear! r/game-repository)
  (w/clear! r/wallet-service))

(defn- create-table []
  (let [table (t/create-new-table)]
    (tr/save-table! r/table-repository table)
    (:id table)))

(clean-up)
(def table-id (create-table))
(ac/start)


;(test/use-fixtures :each (clean-up) (ac/start))

(defn equals [x y]
  (test/is (= x y)))


(test/deftest end-to-end-test
  (with-redefs [g/new-deck (fn [] [[:club :2] [:spade :2] [:diamond :7] [:heart :J]
                                   [:diamond :5] [:heart :3] [:diamond :4] [:heart :2]])
                r/external-event-bus f/fake-ext-event-bus]
    (let [player-name "Paul"
          dealer-name "Dean"
          test-agent (->AppLevelTestAgent)
          player-id (register! test-agent player-name)
          dealer-id (register! test-agent dealer-name)
          get-game-fn #(first (gr/get-games r/game-repository))
          get-balance #(w/get-balance r/wallet-service %)]
      (equals 20000 (get-balance player-id))
      (equals 20000 (get-balance dealer-id))
      (seat-player! test-agent dealer-id table-id)
      (tf/sleep-a-bit)
      (assert-event-sent! test-agent {:event {:table-id table-id
                                              :players [dealer-id]
                                              :type :table-seating-changed-event}
                                      :addressee {:table-id table-id}})
      (seat-player! test-agent player-id table-id)
      (tf/sleep-a-bit)
      (assert-event-sent! test-agent {:event {:table-id table-id
                                              :players [dealer-id player-id]
                                              :type :table-seating-changed-event}
                                      :addressee {:table-id table-id}})
      (let [game (get-game-fn)
            game-id (:id game)]
        (assert-event-sent! test-agent {:event {:game-id game-id
                                                :table-id table-id
                                                :type :game-started-event}
                                        :addressee {:table-id table-id}})
        (do-action! test-agent game-id player-id :hit)
        (do-action! test-agent game-id dealer-id :hit)
        (do-action! test-agent game-id player-id :hit)
        (do-action! test-agent game-id dealer-id :hit)
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
        (do-action! test-agent game-id player-id :stand)
        (assert-event-sent! test-agent {:event {:game-id game-id
                                                :table-id table-id
                                                :player-id player-id
                                                :type :player-stands-event}
                                        :addressee {:table-id table-id}})
        (do-action! test-agent game-id dealer-id :stand)
        (assert-event-sent! test-agent {:event {:game-id game-id
                                                :table-id table-id
                                                :winner player-id
                                                :type :game-finished-event}
                                        :addressee {:table-id table-id}})
        )
      ;;assertions
      (let [game (get-game-fn)
            player (pr/get-player r/player-repository player-id)
            dealer (pr/get-player r/player-repository dealer-id)
            table (tr/get-table r/table-repository table-id)]
        (equals :finished (:state game))
        (equals player-id (g/winner-of game))
        (equals 20500 (get-balance player-id))
        (equals 19500 (get-balance dealer-id))
        (equals 1 (:win-number player))
        (equals 0 (:win-number dealer))
        (empty? (:players table)))
      )))

(test/run-tests)

(println "Yup Finished")
