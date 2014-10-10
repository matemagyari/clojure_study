(ns
  ^{:author mate.magyari}
  clojure_study.other.circuit-breaker)

(defn logger [f]
  (fn [& args]
    (println "Doint it " args)
    (apply f args)))


(defn circuit-breaker
  "This higher order function will return a function with the same signature and behaviour
   as the original function, but with an added functionality.
   The arguments are:
   f - the original function
   max-try-limit - the number of times f can be called unsuccessfully before the circuit breaks
   wait-seconds - how much time after the break is the circuit functioning again
   fail-value - the value the function should return if the circtuit is broken"
  [f max-try-limit wait-seconds fail-value]
  (let [fail-count (atom 0) ; the number of failed attempts since the last reset
        break-ts (atom nil) ; timestamp of last break
        reached-try-limit? #(= max-try-limit @fail-count)
        waited-enough? #(<
                          (* 1000 wait-seconds)
                          (- (System/currentTimeMillis) @break-ts))
        reset-circuit! #(do
                          (reset! fail-count 0)
                          (reset! break-ts nil))]
    (fn [& args]
      (println (str "Fails: " @fail-count))
      (cond
        (reached-try-limit?) (cond
                               (waited-enough?) (do (reset-circuit!) (recur args))
                               :else fail-value)
        :else (try ; if we are below try attempts limit
                (let [result (apply f args)]
                  (do
                    (reset-circuit!) ;if the call is successful, reset the circuit counter
                    result))
                (catch RuntimeException ex
                  (do
                    (swap! fail-count inc)
                    (when (reached-try-limit?)
                      (reset! break-ts (System/currentTimeMillis)))
                    fail-value)))))))

(defn div [a b]
  (println "Doing it")
  (/ a b))

(def safe-div (circuit-breaker / 3 10 "Oops"))
