(ns
  ^{:author mate.magyari}
  mini-projects.blackjack.infrastructure.adapter.driving.shared.locking)

(defn acquire-lock! [locks-ref id]
  (dosync
    (let [the-lock (get @locks-ref id)]
      (if the-lock nil
        (do
          (alter locks-ref assoc id true)
          true)))))

(defn release-lock! [locks-ref id]
  (dosync
    (alter locks-ref dissoc id)))
