(ns
  ^{:author mate.magyari}
  clojure-study.concurrency-primitives
  (:require [clojure-study.assertion :as a]))

;;------------------------------------------------------------------------------- ATOMS -------------------------------------------
(let [counter (atom 0)]
  (do
    (swap! counter #(+ 2 %))
    (a/assert-equals 2 @counter)
    (reset! counter 5)
    (a/assert-equals 5 @counter)))


;;------------------------------------------------------------------------------- REFS -------------------------------------------
;ref-set and alter
(let [location (ref "London")
      salary (ref 100)]
  (dosync
    (ref-set location "NY")
    (alter salary #(+ 50 %)))
  (a/assert-equals @location "NY")
  (a/assert-equals @salary 150))

;rollback
(let [location (ref "London")
      salary (ref 100)
      do-it-wrong (fn []
                    (dosync
                      (ref-set location "NY")
                      (a/assert-equals @location "NY")
                      (throw (RuntimeException.))))]
  (try (do-it-wrong)
    (catch Exception ex (println (str "caught ex" (.getMessage ex)))))
  (a/assert-equals @location "London"))



;;------------------------------------------------------------------------------- WATCHERS -----------------------------------------
;watch-fn is called synchronously
(let [watched-val (atom 0)
      copy-val-1 (atom 0)
      copy-val-2 (atom 0)
      watch-dog-1 (fn [key the-ref old-val new-val]
                    (reset! copy-val-1 [key the-ref old-val new-val]))
      watch-dog-2 (fn [key the-ref old-val new-val]
                    (reset! copy-val-2 [key the-ref old-val new-val]))]
  (do
    (add-watch watched-val :watch-1 watch-dog-1)
    (add-watch watched-val :watch-2 watch-dog-2)
    (reset! watched-val 5)
    (a/assert-equals @copy-val-1 [:watch-1 watched-val 0 5])
    (a/assert-equals @copy-val-2 [:watch-2 watched-val 0 5])
    (remove-watch watched-val :watch-1)
    (remove-watch watched-val :watch-2)))

;;------------------------------------------------------------------------------- VALIDATORS -----------------------------------------


(let [validator-fn #(> % 0)
       a-positive (atom 1 :validator validator-fn)]
  (try (reset! a-positive -1) (catch IllegalStateException ex (println "Not positive")))
  (a/assert-equals 1 @a-positive))

(let [a-negative (agent -1)
      err-collector (atom nil)
      err-handler (fn [agent ex]
                    (reset! err-collector [agent ex]))]
  (do
    (set-validator! a-negative neg?)
    (set-error-handler! a-negative err-handler)
    (try (send a-negative (partial + 1)))
    (Thread/sleep 100)
    (a/assert-equals -1 @a-negative)
    (a/assert-equals (first @err-collector) a-negative)
    (a/assert-equals IllegalStateException (type (second @err-collector)) )))

