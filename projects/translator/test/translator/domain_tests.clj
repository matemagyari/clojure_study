(ns translator.domain-tests
  (:require [clojure.test :refer :all]
            [translator.domain :as domain]
            [translator.in-memory-repository :as imr]
            [translator.in-memory-dictionary :as imt]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]))



