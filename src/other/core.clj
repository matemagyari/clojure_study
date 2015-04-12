(ns
  ^{:author mate.magyari}
  clojure-study.other.core)


;;================= agent-specific functions =============================
(defn- create-actor [state validator err-handler watcher]
  (let [a (agent state
            :validator validator
            :error-handler err-handler
            )]
    (do
      ;(add-watch a :watcher watcher)
      a)))


(defn- send-message! [target f & args]
  (apply send-off (->> args (cons f) (cons target))))

(defn send-message!! [args]
  (apply send-off (flatten args)))

;;=======================================================================

(defn- watch-dog [key the-ref old-val new-val]
  (println (str "Change! " (:name old-val) ": " (:balls old-val) " -> " (:balls new-val))))

(defn ball-types []
  "Possible ball types"
  [:b :w :r :g :y :o])

(defn- random-state-1 [n]
  (take n (repeatedly #(rand-nth (ball-types)))))

(defn- random-state-2 []
  (shuffle (ball-types)))

(def max-throw-count 500)

(defn can-throw? [actor]
  (>= max-throw-count (:throw-counter actor)))

(defn is-valid-actor? [actor]
  "True id actor is in valid state, false otherwise"
  (every? #(contains? actor %) [:name :balls :throw-counter :end])
  ;(can-throw actor)
  (not (empty? (:balls actor))))

(defn- actor-err-handler [actor ex]
  (println (str @actor " has some problem " ex)))

(defn create-actors [n init-balls-num]
  (for [i (range n)]
    (let [state {:name (str "name" i)
                 :balls (random-state-1 init-balls-num)
                 :throw-counter 0 :end false}]
      (create-actor state is-valid-actor? actor-err-handler watch-dog))))
