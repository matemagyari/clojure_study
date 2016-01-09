(ns economy.math-tests
  (:require [clojure.test :refer :all]
            [economy.math :refer :all]))

(deftest delta-conversion-tests
  (let [result (delta-conversion {:dy    20
                                  :x0    3
                                  :f     (fn [a] (* a 10))
                                  :f-inv (fn [a] (/ a 10))})]
    (is (= 2 result))))

(run-tests 'economy.math-tests)
