(ns
  ^{:author mate.magyari
    :doc "Pure functions describing the behaviour of the Rover component"}
  marsrovers.pure.rover
  (:require [marsrovers.pure.rover-move :as move]
            [marsrovers.pure.util :as u]
            [marsrovers.pure.api.nasa-hq-api :as hq]
            [marsrovers.pure.api.rover-api :as r]
            [marsrovers.pure.api.rover-controller-api :as c]
            ))

(defn change-position [position action]
  {:pre [(u/valid-action? action) (some? position)]}
  (condp = action
    :left (update-in position [:facing] move/turn-left)
    :right (update-in position [:facing] move/turn-right)
    :move (assoc position
            :x (+ (:x position) (move/move-x (:facing position)))
            :y (+ (:y position) (move/move-y (:facing position))))))

;;rover-state can be: CREATED, REGISTERED, DEPLOYED, READY, MOVING

(def ^:private msg-end-of-movement {:type :end-of-movement})

(defn- rover-log! [rover & text]
  (u/log! "Rover " (:id rover) ": " text))

(defn- position-msg [rover]
  (r/position-msg (:id rover) (:position rover) (:in-channel rover)))

(defn self-poison-msg [rover]
  (u/msg
    (:in-channel rover)
    (c/posion-pill-msg)))

;;implement different states of the rover with defmulti
(defmulti receive (fn [rover in-msg plateau-channel mediator-channel] (:state rover)))

(defmethod receive :registration [rover in-msg plateau-channel mediator-channel]
  ;(u/log! "Rover " (:id rover) ": Message arrived in :registration state " in-msg)
  (condp = (:type in-msg)
    :tick (when (not= :registered (:state rover))
            (assert (= (:id rover) (:id in-msg)))
            ;(rover-log! rover "Publishing registration")
            {:state rover
             :msgs [(u/msg
                      mediator-channel
                      (apply hq/register-rover-msg ((juxt :id :config :in-channel) rover)))
                    (u/msg
                      (:in-channel rover) in-msg 10)]})
    :rover-registered (do
                        ;(rover-log! rover "Mars rover is registered")
                        {:state (assoc rover :state :registered)})
    ;default
    (do
      (rover-log! rover "Unknown message! " in-msg)
      {:state rover})))

(defmethod receive :dead [rover in-msg plateau-channel mediator-channel]
  ;(rover-log! rover "I'm quite dead yet I have received this message " in-msg)
  {:state rover})


(defmethod receive :default [rover in-msg plateau-channel mediator-channel]
  ;(rover-log! rover "Message arrived in :default state " in-msg)
  (condp = (:type in-msg)

    :deploy-rover (let [rover (assoc rover
                                :position (:rover-position in-msg)
                                :controller-channel (:controller-channel in-msg))]
                    {:state rover
                     :msgs [(u/msg
                              plateau-channel (position-msg rover))]})

    :rover-action (let [delay (if (u/in? (:action in-msg) :left :right)
                                (get-in rover [:config :speed :turning-speed])
                                (get-in rover [:config :speed :movement-speed]))]
                    {:state (assoc rover
                              :last-action (:action in-msg)
                              :state :moving)
                     :msgs [(u/msg
                              (:in-channel rover)
                              msg-end-of-movement
                              delay)]})

    :end-of-movement (let [new-position (change-position (:position rover) (:last-action rover))
                           rover (assoc rover :position new-position)]
                       {:state rover
                        :msgs [(u/msg
                                 plateau-channel (position-msg rover))]})

    :collision (do
                 ;(rover-log! rover "Mars rover has broken down")
                 {:state rover
                  :msgs [(self-poison-msg rover)]})

    :got-lost (do
                ;(rover-log! rover "Mars rover got lost")
                {:state rover
                 :msgs [(u/msg
                          (:controller-channel rover) (position-msg rover))
                        (self-poison-msg rover)]})

    :ack (do
           (condp = (:state rover)
             :registered {:state (assoc rover :state :deployed)
                          :msgs [(u/msg
                                   (:controller-channel rover) (c/rover-deployed-msg))]}
             {:state (assoc rover :state :ready)
              :msgs [(u/msg
                       (:controller-channel rover) (position-msg rover))]}))

    :poison-pill (do
                   (rover-log! rover "I'm dead!")
                   {:state (assoc rover :state :dead)})

    (do
      ;(rover-log! rover "Unknown message ins state " (:state rover) ": " in-msg)
      {:state rover})))

(defn rover-position [x y facing]
  {:x x :y y :facing facing})

(defn rover [id config channel]
  {:pre [(some? (get-in config [:speed :turning-speed]))]}
  {:id id
   :position nil
   :config config
   :last-action nil
   :state :registration
   :in-channel channel})