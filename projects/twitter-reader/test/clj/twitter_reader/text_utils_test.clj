(ns twitter-reader.text-utils-test
  (:require [clojure.test :as test]
            [twitter-reader.text-utils :refer :all]))

(test/deftest seq-of-sets->frequencies-tests
  (test/are [x y] (= x y)
    (seqs->frequencies [#{:a :b} #{:b :c}]) {:a 1 :b 2 :c 1}
    (seqs->frequencies []) {}))


