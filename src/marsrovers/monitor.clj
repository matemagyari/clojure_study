(ns ^{:author mate.magyari
      :doc "All the functions to monitor the app. They are not need to and don't influence other parts of the app.
            They leverage the fact that the components are tied to atoms, so they can be monitored by Clojure watchers"}
  marsrovers.monitor)

(defn rovers-monitor
  "Returns a function to count rover activities and print them on the Console"
  []
  (let [dead-counter (atom 0)
        action-counter (atom 0)]
    (fn [k r old-val new-val]
      (when (not= (:position old-val) (:position new-val))
        (swap! action-counter inc)
        (when (= 0 (mod @action-counter 1000))
          (println (str "Action number " @action-counter))))
      (when (and (= :dead (:state new-val))
              (not= :dead (:state old-val)))
        (swap! dead-counter inc)
        (println (str "Dead rover number " @dead-counter))))))
