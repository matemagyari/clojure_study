(ns
  ^{:author mate.magyari}
  marsrovers.util
  (:require [clojure.core.async :as a]))

;;util
(defn in? [elem & vals]
  (some #(= elem %) vals))

(defn log! [& args]
  (println (apply str (flatten args))))

(defn valid-action? [action]
  (in? action :left :right :move))

(defn msg
  ([target body]
    (msg target body nil))
  ([target body delay]
    {:target target :body body :delay delay}))

;;========= CORE.ASYNC ===============

(defn send-msg! [msg]
  {:pre [(some? (:target msg)) (some? (:body msg))]}
  (a/go
    (if-let [delay (:delay msg)]
      (a/<! (a/timeout (* delay 10))))
    (a/>! (:target msg) (:body msg))))

(defn send-msgs! [msgs]
  (doseq [msg msgs] (send-msg! msg)))

(defn chan []
  (a/chan 100))

(defn process-result! [entity-atom {state :state
                                    msgs :msgs
                                    effects :effects}]
  (when state
    (reset! entity-atom state))
  (when effects
    (doseq [e! effects] (e!)))
  (when msgs
    (send-msgs! msgs)))


(defn start-component! [entity-atom in-channel msg-processing-fn]
  (a/go-loop []
    (when-let [in-msg (a/<! in-channel)]
      (let [result (msg-processing-fn in-msg)]
        (process-result! entity-atom result)))
    (recur)))



;;==============
