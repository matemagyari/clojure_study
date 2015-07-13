(ns carfactory.factory-async
  "Car Factory implemented using go blocks and channels from core.async"
  (:require [clojure.core.async :as async]
            [carfactory.core :as core]))

; pipe capacity is 10. If full, will block the sender.
(defn- conveyor-belt
  "Creates a conveyor-belt"
  [& pars]
  (let [xf (:with-worker (apply hash-map pars)) ; process every item going through the pipe with 'xf' processor
        handle-ex (fn [ex] (println "Exception: " ex))] ;print any exceptions happening on the pipe
    (async/chan 10 xf handle-ex)))

(defn- create-painter-worker
  "Creates a worker to paint with the given color"
  [color]
  (map #(core/paint % color)))

(defn- start-cars-assembler-worker!
  "Starts up a worker that will pull parts from its in-pipes and puts assembled cars on its out-pipe"
  [{:keys [wheels-in engines-in coachworks-in cars-out]}]
  (async/go-loop []
    (let [coachwork (async/<! coachworks-in) ; pick up an coachwork from the coachwork queue
          engine (async/<! engines-in) ; pick up an engine from the engine queue
          wheels [(async/<! wheels-in) ; pick up 4 wheels from the wheels queue
                  (async/<! wheels-in)
                  (async/<! wheels-in)
                  (async/<! wheels-in)]
          car (core/assemble-car engine coachwork wheels)]
      (async/>! cars-out car)
      (recur))))

(defn- start-random-splitter!
  "Gets cars coming on the in queue and randomly puts each on one of the out queues"
  [{:keys [in-pipe out-pipes]}]
  (async/go-loop []
    (when-let [item (async/<! in-pipe)]
      (async/>! (rand-nth out-pipes) item))
    (recur)))

(defn create-factory
  "Creates a car factory. Pipes the the workers together.
  Returns a map containing the input channels and the output channel of the factory."
  []
  (let [source-wheels (conveyor-belt :with-worker (filter core/flawless?))
        source-engines (conveyor-belt :with-worker (filter core/flawless?))
        source-coachworks (conveyor-belt :with-worker (filter core/flawless?))
        pipe-assembled-cars (conveyor-belt)
        pipe-to-green-painter (conveyor-belt :with-worker (create-painter-worker :green))
        pipe-to-blue-painter (conveyor-belt :with-worker (create-painter-worker :blue))
        pipe-to-red-painter (conveyor-belt :with-worker (create-painter-worker :red))]
    (start-cars-assembler-worker! {:wheels-in source-wheels
                                   :engines-in source-engines
                                   :coachworks-in source-coachworks
                                   :cars-out pipe-assembled-cars})
    (start-random-splitter! {:in-pipe pipe-assembled-cars
                             :out-pipes [pipe-to-green-painter
                                         pipe-to-blue-painter
                                         pipe-to-red-painter]})
    {:source-wheels source-wheels
     :source-engines source-engines
     :source-coachworks source-coachworks
     :sink-cars (async/merge [pipe-to-green-painter pipe-to-blue-painter pipe-to-red-painter])}))

;; ===================== Producers and Consumers to run the Factory with =================================
(defn supply-parts! [{:keys [to produce-item-fn]}]
  (async/go-loop []
    (let [part (core/mark-item (produce-item-fn))]
      (async/>! to part)
      (recur))))

(def ^:private car-count (atom 0))

(defn consume-product!
  "Consumes from the queue and increments the counter"
  [queue]
  (async/go-loop []
    (when-let [car (async/<! queue)]
      ;(println car)
      (swap! car-count inc))
    (recur)))

(defn run-factory!
  "Runs the factory instance by supplying inputs and consuming the output"
  [{:keys [source-wheels source-engines source-coachworks sink-cars] :as factory}]
  (consume-product! sink-cars)
  (supply-parts! {:to source-wheels :produce-item-fn core/produce-wheel})
  (supply-parts! {:to source-engines :produce-item-fn core/produce-engine})
  (supply-parts! {:to source-coachworks :produce-item-fn core/produce-coachwork}))


;; Run a factory for some time and print out the number of cars coming out of it.
(defn -main [& args]
  (println "Starting factory...")
  (run-factory! (create-factory))
  (async/<!! (async/timeout 10000))
  (println "Number of cars produced: " @car-count))


