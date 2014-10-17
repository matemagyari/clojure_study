(ns ^{:author mate.magyari
      :doc "All the functions to monitor the app. They are not need to and don't influence other parts of the app.
            They leverage the fact that the components are tied to atoms, so they can be monitored by Clojure watchers"}
  marsrovers.monitor)

(defn- state-changed? [old-val new-val]
  (not= (:state new-val) (:state old-val)))

(defn- state-changed-to? [old-val new-val state]
  (and (state-changed? old-val new-val)
    (= state (:state new-val))))

(defn rovers-monitor
  "Returns a function to count rover activities and print them on the Console"
  []
  (let [dead-counter (atom 0)
        action-counter (atom 0)
        registered-counter (atom 0)
        print-status! #(println (str "Action number " @action-counter
                                  " Registered rover number " @registered-counter
                                  " Dead rover number " @dead-counter))]
    (fn [k r old-val new-val]
      (when (not= (:position old-val) (:position new-val))
        (swap! action-counter inc)
        (when (= 0 (mod @action-counter 10000))
          (print-status!)))
      (when (state-changed-to? old-val new-val :registered)
        (swap! registered-counter inc)
        (print-status!))
      (when (state-changed-to? old-val new-val :dead)
        (swap! dead-counter inc)
        (print-status!)))))
