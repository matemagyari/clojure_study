## Parallel computation with pipes and filters ##

The task is to simulate a car factory that has multiple, independent workers connected to each other by conveyor belts. The stages of the process:
* The Factory has 3 in-queues, one for engines, one for coachworks and one for wheels arriving into the Factory continuously.
* workers to filter out the faulty parts operate on each in-queue. 
* The intact parts are carried further on conveyor belts to a worker that assembles a car from each 'coachwork + engine + 4 wheels' combination. 
* The cars then are then moved to a worker that randomly puts each onto one of the conveyor belts towards 3 painter workers.
* Finally all the conveyors from the painter stations ebb into one, the out-queue of the factory.

The Challenge is (minor) part a modelling task, (major) part a parallel processing exercise. All the workers work independently and leveraging the parallel nature of the factory-process higher throughput is achievable than with a sequential solution. The aim is to maximize the number of cars produced in a fixed amount of time.

Figure comes here.

### Workers ###

WS-FE: filters out faulty engines. Only "healthy" ones are let trough.
WS-FC: filters out faulty coachworks
WS-FW: filters out faulty wheels

W-CA: assembles cars from each 'coachwork + engine + 4 wheels' combination
W-S: puts incoming cars on a random out-queue

W-PR: paints cars to red
W-PB: paints cars to blue
W-PG: paints cars to green

W-M: merges the 3 conveyor belts into one

### For the simulation you should implement the following as concurrent, independent processes ###

* the Factory, obviously
* a Consumer that will consume the cars rolling out of the Factory and keeps counting them to measure the throughput of the Factory
* 3 Providers to supply continuous streams of engines, wheels and coachworks, respectively


### Hints ###

* the domain model of the factory (Engine, Wheel, Coachwork, Car, Workers) should be as simple as possible as they only provide the pretext for the challenge. Faulty parts could be simply marked with a flag and painting could be simply setting a field. The emphasis should be on the parallelization. The chosen approach (see next section) will probably heavily influence the model.
* To simulate CPU-intensive work at the workers simply run a fixed-length for-loop to increment a number.

### Possible approaches/technologies to use ###

This problem can be tackled using very different approaches. Some examples are mentioned below. If you are familiar with either of those, it's not difficult to implement. If you find it too easy or quick, we'd advise you to do it with other approaches as well and compare them.

 * CSP: e.g. Go, Clojure, Java CSP, ...
 * Actor model: e.g. Akka (Scala/Java)
 * Dataflow programming/FRP: e.g. Scala/Java 8 streams, RxJava/RxScala/RxClojure/RxJS/...
 * Fork-Join framework of Java 7
  
### Caveats ###

* The Factory should be prepared for high load. What if parts coming faster than it can process them (4 wheels for one car)? 
