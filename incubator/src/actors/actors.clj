(ns actors.actors
  (:require [clojure.core.async :as async]))

(defprotocol Actor
  "Actor interface"
  (in-chan [this] "Returns the in-channel of the actor")
  (out-chan [this] "Returns the out-channel of the actor")
  (get-state [this] "Returns the state of the actor")
  (shut-down! [this] "Shuts down the actor"))


(defn create-actor!
  "Creates an actor who processes messages coming to its in-channel.
  Its behaviour is a (state,msg)=>(state,[msg]) function.
  The output messages are put on its out-channel.
  If it's shut down, it will consume and discard incoming messages."
  [{:keys [in out init-state behaviour]}]
  (let [alive? (atom true)
        state (atom init-state)
        actor (reify Actor
                (in-chan [this] in)
                (out-chan [this] out)
                (get-state [this] @state)
                (shut-down! [this]
                  (reset! alive? false)
                  (async/close! out)))
        send-msgs! (fn [ms] (async/go (doseq [m ms]
                                        (async/>! out m))))]
    (async/go-loop []
      (when-let [msg (async/<! in)]
        (when @alive?
          (let [r (behaviour {:state @state :msg msg})]
            (send-msgs! (:msgs r))
            (reset! state (:state r))))
        (recur)))
    actor))

(defn keep-and-report
  "A valid function for an actor's behaviour. Attaches the message to the state and sends out the state as a message."
  [{:keys [state msg]}]
  (let [new-state (conj state msg)]
    {:state new-state
     :msgs [(str "state: " new-state)]}))

;; Create a simple actor
(def actor-1 (create-actor! {:in (async/chan)
                             :out (async/chan)
                             :init-state nil
                             :behaviour keep-and-report}))

(def in (in-chan actor-1))
(def out (out-chan actor-1))

;; Print out the messages sent by the actor
(async/go-loop []
  (when-let [msg (async/<! out)]
    (println (str "Message sent by actor: " msg))
    (recur)))

(async/>!! in 1)
;;Message sent by actor: state: (1)
(async/>!! in 2)
;;Message sent by actor: state: (2 1)
(assert (= [2 1] (get-state actor-1)))

(shut-down! actor-1)

(async/>!! in 3)
(async/>!! in 4)
;;shut down actor ignores messages
(assert (= [2 1] (get-state actor-1)))


