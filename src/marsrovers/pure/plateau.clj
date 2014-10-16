(ns ^{:author mate.magyari
      :doc "Pure functions describing the behaviour of the Plateau component"}
  marsrovers.pure.plateau
  (:require [marsrovers.pure.api.rover-api :as ra]
            [marsrovers.pure.api.plateau-api :as pa]
            [marsrovers.pure.util :as u]))

;; -----------------  private functions ------------------------

(defn- is-rover-lost? [rover-position plateau-config]
  (let [out-range? (fn [n r]
                     (or (neg? n) (> n r)))]
    (or
      (out-range? (:x rover-position) (:x plateau-config))
      (out-range? (:y rover-position) (:y plateau-config)))))

(defn- collisions-msgs [plateau rover-id rover-position]
  (for [rover (-> plateau :rover-positions vals)
        :when (and
                (= rover-position (:rover-position rover))
                (not= rover-id (:rover-id rover)))]
    (u/msg
      (:rover-channel rover)
      (pa/collision-msg))))

(defn- plateau-view [plateau]
  (let [rovers-positions (->> plateau :rover-positions vals (map :rover-position)
                           (map (fn [r] (select-keys r [:x :y]))))
        [x-dim y-dim] ((juxt :x :y) (:config plateau))
        rover? (fn [x y] (some #(= % {:x x :y y}) rovers-positions))]
    (for [x (range x-dim)]
      (for [y (range y-dim)]
        [(if (rover? x y) :moving :empty) x y]))))

(defn- plateau-view-msg [plateau]
  (u/msg (:displayer-channel plateau) (plateau-view plateau)))

;; -----------------  public functions ------------------------

(defn receive [plateau in-msg]
  (condp = (:type in-msg)

    :position (let [rover-id (:rover-id in-msg)
                    rover-channel (:rover-channel in-msg)
                    rover-position (:rover-position in-msg)
                    plateau (update-in plateau [:rover-positions]
                              conj [rover-id in-msg])]
                {:state plateau
                 :msgs (let [view-msg (plateau-view-msg plateau)
                             coll-msgs (collisions-msgs plateau rover-id rover-position)]
                         (cond
                           (not (empty? coll-msgs)) coll-msgs
                           (is-rover-lost? rover-position (:config plateau)) [(u/msg rover-channel (pa/got-lost-msg)) view-msg]
                           :else [(u/msg rover-channel (pa/ack-msg)) view-msg])
                         )})

    ;;default
    {:state plateau}))

(defn plateau [config in-channel displayer-channel]
  {:rover-positions {}
   :config config
   :in-channel in-channel
   :displayer-channel displayer-channel})
