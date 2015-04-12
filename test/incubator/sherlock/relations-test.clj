(ns
  ^{:author mate.magyari}
  sherlock.relations-test
  (:require [clojure.test :as test]
            [sherlock.relations :as r]
            [sherlock.sherlock :as s]))

(def w (-> (r/new-world)
         (r/define-rooms :r1 :r2)
         (r/define-rooms :r2 :r3)))

(defn assert-eq [x y]
  (test/is (= x y)))

(defn is-not [x]
  (test/is (false? x)))

(test/deftest house-test
  (let [world (-> (r/new-world)
                (r/define-rooms :r1 :r2)
                (r/define-rooms :r2 :r3)
                (r/define-rooms :r3 :r4))
        n (r/neighbours world :r2)]
    (assert-eq (set [:r1 :r3]) (set n))
    (test/is (r/valid-path? world :r1 :r2 :r3 :r4))
    (is-not (r/valid-path? world :r1 :r2 :r4))))

(test/run-tests)


