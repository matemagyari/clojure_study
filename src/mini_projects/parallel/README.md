Parallel computation with pipes and filters

The task is to simulate a car factory that has multiple, independent workstations connected to each other by conveyor belts.
The Factory has 3 in-queues, one for engines, one for coachworks and one for wheels arriving into the Factory.
Workstations to filter out the faulty parts operate on each in-queue.
The intact parts are carried further by the conveyor belts to a workstation that assembles a car from each 'coachwork + engine + 4 wheels' combination.
The cars are then moved to a workstation that randomly puts them on conveyor belts towards painter workstations. Finally all the conveyors from the painter
stations ebb into one, the out-queue of the factory.

The Challenge is (minor) part a modelling task, (major) part a parallel processing exercise. All the workstations work independently and leveraging the parallel nature
of the factory-process higher throughput is achievable than with of a sequential solution. The aim is to maximize the number of cars produced in a fixed amount of time.

Figure comes here.

Workstations

WS-FE: filters out faulty engines
WS-FC: filters out faulty coachworks
WS-FW: filters out faulty wheels

W-CA: assembles cars from each 'coachwork + engine + 4 wheels' combination
W-S: puts incoming cars on a random out-queue

W-PR: paints cars to red
W-PB: paints cars to blue
W-PG: paints cars to green

W-M: merges the 3 conveyor belts into one

For the simulation you should implement the following as concurrent, independent processes

* the Factory, obviously
* a Consumer that will consume the cars rolling out of the Factory and keeps counting them to measure the velocity of the Factory
* 3 Providers to supply engines, wheels and coachworks, respectively


Hints
* the domain model of the factory (Engine, Wheel, Coachwork, Car, Workstations) should be as simple as possible as they only provide the pretext of the challenge.
  Faulty parts could be simply marked with a flag and painting could be simply setting a field. The emphasis should be on the parallelization.
* To simulate CPU-intensive work at the workstations simply run a fixed-length for-loop to increment a number.
* Go's and Clojure's channels are ideal candidates for implementation, but
  the task could also be tackled with the streams or actors of Scala/Java's Akka, or the Fork-Join framework of Java 7.