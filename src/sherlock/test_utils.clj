(ns
  ^{:author mate.magyari}
  sherlock.test-utils)

(defn is= [x y]
  (test/is (= x y)))

(defn is-not [x]
  (test/is (false? x)))
