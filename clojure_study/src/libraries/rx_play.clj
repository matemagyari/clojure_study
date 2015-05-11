(ns libraries.rx-play
  (:import [rx Observable]
           [java.util.concurrent TimeUnit])
  (:require [rx.lang.clojure.core :as rx]))

(defn prn [value]
  (println "Value: " value))

(let [obs (rx/return 10)]
  (rx/subscribe obs prn))

(println "===================")

(let [obs (rx/seq->o [1 2 3])]
  (rx/subscribe obs prn))

(println "===================")

(let [obs (rx/range 2 5)]
  (rx/subscribe obs prn))

(println "===================")

; ================== Built-in observables
(let [obs (Observable/interval 10 TimeUnit/MILLISECONDS)
      subs (rx/subscribe obs prn)]
  (Thread/sleep 100)
  (rx/unsubscribe subs))



(println "===================")

; ================== Create custom observable =====================
(let [just-obs (fn [v]
                 (rx/observable*
                   (fn [observer]
                     (rx/on-next observer v)
                     (rx/on-completed observer))))]
  (rx/subscribe (just-obs 20) prn))

(println "===================")
; ================== FP operations =====================
(let [obs (->> (rx/range 10)
            (rx/filter even?)
            (rx/map inc)
            (rx/take 5)
            (rx/reduce +))]
  (rx/subscribe obs #(println "Val: " %)))

(println "===================")
; ================== FP operations =====================
(let [obs1 (rx/range 10)
      obs2 (rx/seq->o [:a :b :c :d :e :f])
      obs-m (rx/merge obs1 obs2)]
  (rx/subscribe obs-m #(println "Valm: " %)))

(println "===================")

; ================== ZIP observables =====================
(let [obs1 (rx/range 10)
      obs2 (rx/seq->o [:a :b :c :d :e :f])
      obs-m (rx/map vector obs1 obs2)
      prn (fn [[v1 v2]]
            (println v1 "-" v2))]
  (rx/subscribe obs-m prn))

(println "===================")

;;buffer
(let [obs1 (rx/range 10)
      obs2 (.buffer obs1 2)]
  (rx/subscribe obs2 prn))

(println "buffer ===================")

;;multiple observers
(let [obs (rx/range 10)]
  (rx/subscribe obs #(println "s1" %))
  (rx/subscribe obs #(println "s2" %)))

(println "buffer ===================")

; ================== merge observables =====================
(let [obs1 (rx/range 10)
      obs2 (rx/range 10)
      obs-m (rx/map + obs1 obs2)
      prn (fn [v]
            (println v))]
  (rx/subscribe obs-m prn))

(println "===================")
