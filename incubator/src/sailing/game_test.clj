(ns
  ^{:author mate.magyari}
  sailing.game-test
  (:require [sailing.game :as g]
            [clojure.test :as t]))

(defn- is= [x y]
  (t/is (= x y)))

(t/deftest questionare-test
  (let [result (g/questionare 3 4 (range 10))]
    (println result)
    (is= 3 (count result))
    (doseq [r result]
      (t/is (g/seq-contains? (second r) (first r)))
      (is= 4 (-> r second count)))))

(t/run-tests)
