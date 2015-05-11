(ns carfactory.factory-rx
  (:import [rx Observable]
           [java.util.concurrent TimeUnit])
  (:require [carfactory.core :as core]
            [rx.lang.clojure.core :as rx]))

(comment
  "Car Factory implemented using RX")

(defn- serialnum-mod? [car n]
  (= n
    (mod (:serialnum car) 3)))

(defn split-car-xs
  "Splits the car stream into 3 streams based on serialnumbers"
  [car-xs]
  (let [->xs (fn [n]
               (rx/filter #(serialnum-mod? % n) car-xs))]
    [(->xs 0)
     (->xs 1)
     (->xs 2)]))

(defn produce-cars
  "Transforms the input 3 streams of engines, coachworks and wheels, respectively, into a car stream and returns it."
  [{:keys [engines-xs coachworks-xs wheels-xs]}]
  (let [filter-xs (fn [xs]
                    (rx/filter core/flawless? xs))
        ;zip the 3 streams into one, appying the assemble-car function to their elements
        car-xs (rx/map core/assemble-car
                 (filter-xs engines-xs)
                 (filter-xs coachworks-xs)
                 (.buffer (filter-xs wheels-xs) 4)) ;buffer the wheels stream to create a stream of 4-wheels
        [g-xs b-xs r-xs] (split-car-xs car-xs)] ;split up the car stream into 3 streams
    (rx/merge ;transform each streams with the 'paint' then merge them into one
      (rx/map #(core/paint % :green) g-xs)
      (rx/map #(core/paint % :blue) b-xs)
      (rx/map #(core/paint % :red) r-xs))))

; frequency of faulty items arriving in the factory
(defn- create-endless-xs [interval elem-fn]
  (let [mapf (fn [_] (core/mark-item (elem-fn)))]
    (as-> (Observable/interval interval TimeUnit/MICROSECONDS) $
      (.onBackpressureDrop $)
      (rx/map mapf $))))

(def ^:private interval 5)

(def car-xs (produce-cars {:engines-xs (create-endless-xs (* 4 interval) core/produce-engine)
                           :coachworks-xs (create-endless-xs (* 4 interval) core/produce-coachwork)
                           :wheels-xs (create-endless-xs interval core/produce-wheel)}))

(def ^:private car-count (atom 0))

(defn run [time]
  (rx/subscribe car-xs
    (fn [c] ;on-next
      ;(println c)
      (swap! car-count inc))
    (fn [e] (println "Error" e)) ;on-error
    (fn [] (println "Completed"))) ;on-completed
  (Thread/sleep time)
  (println @car-count))

;(run 10000)

