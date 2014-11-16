(ns
  ^{:author mate.magyari}
  clojure-study.ideas.timeouter
  (:require [clojure.core.async :as async]))

(defn add-timeout
  "f - the original function
   timeout-ms - timeout in milliseconds
   timeout-value - the value the function should return in case of timeout"
  [f timeout-ms timeout-value]
  (fn [& args]
    (let [timeout-channel (async/timeout timeout-ms)
          result-channel (async/thread (apply f args))
          [r c] (async/alts!! [timeout-channel result-channel])]
      (if (= c timeout-channel)
        timeout-value ;; if timeout
        r))))    ;; if successful

;a slow function
(defn slow-fn []
  (Thread/sleep 100)
  :slow)

;a fast function
(defn fast-fn []
  (Thread/sleep 10)
  :fast)

;add timeouts to both
(def slow-with-timeout (add-timeout slow-fn 50 :timeout))
(def fast-with-timeout (add-timeout fast-fn 50 :timeout))

;see the results
(println "Here comes the slow:" (slow-with-timeout))
(println "Here comes the fast:" (fast-with-timeout))