(ns
  ^{:author mate.magyari
    :doc "General utility functions"}
  marsrovers.pure.util
  (:require [clojure.core.async :as a]))

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


(defn sampler-filter
  "Wraps around a function returning a new function with the same signature, which delegates to the original function, but only
   with the given frequency
   f - the original function
   freq - frequency
   E.g. 'f' is 'println' and 'freq' is 3, then calling the result function only will print at every 3rd time"
  [f freq]
  (let [counter (atom 0)]
    (fn [& args]
      (swap! counter inc)
      (when (= freq @counter)
        (reset! counter 0)
        (apply f args)))))


