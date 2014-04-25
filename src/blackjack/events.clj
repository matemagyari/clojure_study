(ns blackjack.events)

(def event-buffer (ref []))

(defn publish-event [event]
  (println event)
  (dosync
    (alter event-buffer conj event)))

(defmulti handle-event :type)
(defmethod handle-event :player-card-dealt-event [event]
  (println (str "CardDealt! " event)))
(defmethod handle-event :game-finished-event [event]
  (println (str "Finished! " event)))
(defmethod handle-event :default [event]
  (println (str "Unknown " event)))

(defn flush-events []
  (while (not-empty @event-buffer)
    (let [event (first @event-buffer)]
      (dosync
        (alter event-buffer rest))
      (handle-event event))))
