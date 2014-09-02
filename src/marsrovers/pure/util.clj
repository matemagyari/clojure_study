(ns
  ^{:author mate.magyari}
  marsrovers.pure.util
  (:require [clojure.core.async :as a]))

;;util
(defn in? [elem & vals]
  (some #(= elem %) vals))

(defn log! [& args]
  (println (apply str (flatten args))))

(defn valid-action? [action]
  (in? action :left :right :move))

(defn valid-result? [result]
  (some? (:state result)))

(defn msg
  ([target body]
    (msg target body nil))
  ([target body delay]
    {:target target :body body :delay delay}))


