(ns translator.main
  (:require [translator.domain :as d]
            [clojure.spec.alpha :as s]
            [translator.in-memory-repository :as imr]
            [translator.in-memory-dictionary :as imd]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]))

(def context {:context/repository (imr/build-in-memory-repository)
              :context/dictionary (imd/build-in-memory-dictionary)})

(defn run-tests [context]
  (let [context-gen (gen/return context)]
    (stest/check `d/process
                 {:gen                          {::d/context (fn [] context-gen)}
                  :clojure.spec.test.check/opts {:num-tests 100}})))

(run-tests context)

(println "start")