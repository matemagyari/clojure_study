(ns
  ^{:author mate.magyari}
  patterns.component
  (:require [clojure.core.async :as async]))

(defn start-component!
  [flag-atom in-channel msg-processing-fn]
  (async/go-loop []
    (if @flag-atom
      (when-let [in-msg (async/<! in-channel)]
        (msg-processing-fn in-msg)
        (recur))
      (println "Closed"))))

(defn start-stateful-component!
  [flag-atom in-channel init-state msg-processing-fn]
  (async/go-loop [state init-state]
    (if @flag-atom
      (when-let [in-msg (async/<! in-channel)]
        (let [new-state (msg-processing-fn state in-msg)]
          (recur new-state)))
      (println "Closed"))))

(def flag (atom true))

(def in1 (async/chan))
;(defn printer [msg] (println (str "Msg: " msg)))
(defn printer [state msg]
  (println (str "Msg: " msg " state: " state))
  (inc state))

(start-component! flag in1 printer)

(start-stateful-component! flag in1 0 printer)

(async/>!! in1 "z")

(async/close! c1)

(reset! flag false)