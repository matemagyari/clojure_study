(ns mini-projects.parallel.factory
  (:require [clojure.core.async :as async]))

(def ^:private work-load-unit (* 0 100))

; pipe capacity is 100. If full, will block the sender.
(defn- a-pipe
  "Creates a pipe"
  [& pars]
  (let [xf (:with-processor (apply hash-map pars)) ; process every item going through the pipe with 'xf' processor
        handle-ex (fn [ex] (println "Exception: " ex))] ;print any exceptions happening on the pipe
    (async/chan 100 xf handle-ex)))

(defn- exercise-cpu [length]
  "Simply to simulate real work on the CPU"
  (let [x (atom 0)]
    (dotimes [i (* work-load-unit length)]
      (swap! x inc))))

(defn- exercise-cpu-tr
  "Transducer to do CPU work and return identity"
  [length]
  (fn [f]
    (fn [result input]
      (exercise-cpu (* work-load-unit length))
      (f result input))))

(defn- create-faulty-filtering-workstation
  "Creates a workstation that filters out faulty parts" []
  (comp
    (exercise-cpu-tr 4)
    (filter #(nil? (:faulty %)))))

(defn- create-painter-workstation
  "Creates a workstation to paint with the given color"
  [color]
  (let [paint (fn [car] (assoc car :color color))]
    (comp
      (exercise-cpu-tr 4)
      (map #(paint %)))))

(defn- assemble-car
  "Assemble a car from its parts"
  [{:keys [engine wheels coachwork] :as car}]
  (exercise-cpu 4)
  (assoc car :type :car))

(defn- start-cars-assembler-workstation!
  "Starts up a workstation that will pull parts from its in-pipes and puts assembled cars on its out-pipe"
  [{:keys [wheels-in engines-in coachworks-in cars-out]}]
  (async/go-loop []
    (let [coachwork (async/<! coachworks-in) ; pick up an coachwork from the coachwork queue
          engine (async/<! engines-in) ; pick up an engine from the engine queue
          wheels [(async/<! wheels-in) ; pick up 4 wheels from the wheels queue
                  (async/<! wheels-in)
                  (async/<! wheels-in)
                  (async/<! wheels-in)]
          car (assemble-car {:engine engine :coachwork coachwork :wheels wheels})]
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
  "Creates a car factory. Pipes the the workstops together.
  Returns a map containing the input channels and the output channel of the factory."
  []
  (let [source-wheels (a-pipe :with-processor (create-faulty-filtering-workstation))
        source-engines (a-pipe :with-processor (create-faulty-filtering-workstation))
        source-coachworks (a-pipe :with-processor (create-faulty-filtering-workstation))
        pipe-assembled-cars (a-pipe)
        pipe-to-green-painter (a-pipe :with-processor (create-painter-workstation :green))
        pipe-to-blue-painter (a-pipe :with-processor (create-painter-workstation :blue))
        pipe-to-red-painter (a-pipe :with-processor (create-painter-workstation :red))]
    (start-cars-assembler-workstation! {:wheels-in source-wheels
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
; frequency of faulty items arriving in the factory
(def ^:private fault-rate 0.00005)

(defn- mark-item
  "With a given probability marks item as faulty"
  [item]
  (let [max 100
        threshold (* max fault-rate)
        faulty? (> threshold (rand-int max))]
    (if faulty? (assoc item :faulty true) item)))

(defn supply-parts! [{:keys [to produce-item-fn]}]
  (async/go-loop []
    (let [part (mark-item (produce-item-fn))]
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

(defn- produce-engine
  "Produces an engine" []
  {:type :engine})

(defn- produce-wheel
  "Produces a wheel" []
  {:type :wheel})

(defn- produce-coachwork
  "Produces a coachwork" []
  {:type :coachwork})

(defn run-factory!
  "Runs the factory instance by supplying inputs and consuming the output"
  [{:keys [source-wheels source-engines source-coachworks sink-cars] :as factory}]
  (consume-product! sink-cars)
  (supply-parts! {:to source-wheels :produce-item-fn produce-wheel})
  (supply-parts! {:to source-engines :produce-item-fn produce-engine})
  (supply-parts! {:to source-coachworks :produce-item-fn produce-coachwork}))

;; Run a factory for some time and print out the number of cars coming out of it.
(println "Starting factory...")
(run-factory! (create-factory))
(async/<!! (async/timeout 10000))
(println "Number of cars produced: " @car-count)


