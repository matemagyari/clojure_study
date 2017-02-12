(ns economy.manual-tests
  (:require [clojure.test :refer :all]
            [economy.actor :as actor]
            [economy.trading :refer :all]
            [clojure.spec :as s]
            [clojure.spec.test :as stest]
            [clojure.spec.gen :as gen]))


(s/fdef fizz-buzz
        :args int?
        :ret #{"fizz", "buzz", "fizzbuzz"}
        :fn (fn [x]
              (let [input (:args x) output (:ret x)]
                (cond
                  (zero? (mod input 3)) (= output "fizz")
                  (zero? (mod input 5)) (= output "buzz")
                  :else "fizzbuzz"))))

(s/fdef is-zero?
        :args int?
        :ret boolean?
        :fn (fn [x]
              (let [input (:args x) output (:ret x)]
                (if (zero? input)
                  (= output true)
                  false))))
