(ns
  ^{:author mate.magyari}
  clojure-study.other.actors
  (:require [clojure-study.other.core :refer :all]))

(def global-throw-counter (atom 0))

;basic utility
(defn- has-x-of-the-same? [coll x]
  (->> coll (group-by identity) vals (some #(<= x (count %)))))


(defn- stop [actor]
  (assoc actor :end true))

(defn- throw-ball [actor]
  "Choose a ball and change state"
  (let [ball-to-throw (last (:balls actor))
        new-state (-> actor
                    (update-in [:balls] butlast)
                    (update-in [:throw-counter] inc))]
    [ball-to-throw new-state]))

(defn- get-others [me all-actors]
  (-> (dissoc all-actors (:name me)) vals))

(defn- state-and-messages [name state-with-new-ball others catch-fn all-actors]
  {:pre [(string? name) (is-valid-actor? state-with-new-ball)
         (fn? catch-fn) (map? all-actors)]}
  (cond
    (has-x-of-the-same? (:balls state-with-new-ball) 11) (let [messages (for [o others] [o stop])
                                                               state (stop state-with-new-ball)]
                                                           [state messages])
    (can-throw? state-with-new-ball) (let [[ball-to-throw state-after-throw] (throw-ball state-with-new-ball)
                                          message [(rand-nth others) catch-fn ball-to-throw all-actors]]
                                      [state-after-throw (list message)])
    :else [state-with-new-ball []]))

(defn catch-the-ball [state ball all-actors]
  (swap! global-throw-counter inc)
  (cond
    (:end state) state

    :else (let [state-with-new-ball (update-in state [:balls] #(cons ball %))
                others (get-others state all-actors)
                [final-state messages-to-send] (state-and-messages (:name state)
                                                 state-with-new-ball
                                                 others
                                                 catch-the-ball
                                                 all-actors)]
            (do (doseq [t messages-to-send] (send-message!! t))
              final-state))))

(def num-of-agents 5)
(def init-balls-num 10)

(def actor-map (->> (create-actors num-of-agents init-balls-num)
                 (map (fn [a] [(:name (deref a)) a]))
                 (into {})))

(println "Init")
(doseq [a (vals actor-map)]
  (println a))
(println "Start")

(send-message!! [(-> actor-map vals first) catch-the-ball :b actor-map])
(Thread/sleep 2000)
(shutdown-agents)
(println "States and errors")
(doseq [a (vals actor-map)]
  (println a)
  (println (agent-errors a)))
(println "global-throw-counter" @global-throw-counter)
