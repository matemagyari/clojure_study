(ns carfactory.core
  "Car Factory basic functions")

;;we can tune the CPU-intesitivity of the workers
(def ^:private work-load-unit (* 1 0))

(defn- exercise-cpu!
  "Simply to simulate real work on the CPU"
  []
  (let [x (atom 0)]
    (dotimes [i work-load-unit]
      (swap! x inc))))

(defn flawless?
  "Returns true if the item is not faulty"
  [item]
  (exercise-cpu!)
  (nil? (:faulty item)))

(defn paint
  "Paints the car to the given color"
  [car color]
  (exercise-cpu!)
  (assoc car :color color))

(defn assemble-car
  "Assembles a car from its parts"
  [engine coachwork wheels]
  (exercise-cpu!)
  {:engine engine
   :coachwork coachwork
   :wheels wheels
   :type :car
   :serialnum (rand-int 99999999)})

; frequency of faulty items arriving in the factory
(def fault-rate 0.00005)


(defn mark-item
  "With a given probability marks item as faulty"
  [item]
  (let [max 100
        threshold (* max fault-rate)
        faulty? (> threshold (rand-int max))]
    (if faulty? (assoc item :faulty true) item)))


(defn produce-engine
  "Produces an engine" []
  {:type :engine})

(defn produce-wheel
  "Produces a wheel" []
  {:type :wheel})

(defn produce-coachwork
  "Produces a coachwork" []
  {:type :coachwork})
