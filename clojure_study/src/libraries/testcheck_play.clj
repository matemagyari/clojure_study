(ns
  ^{:author mate.magyari}
  clojure-study.libraries.testcheck-play
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

;(def sort-idempotent-prop
;  (prop/))

(defn print-new-lines [& line]
  (doseq [l line]
    (println l)))

(print-new-lines
  (gen/sample gen/int)
  (take 5 (gen/sample-seq gen/int))
  (gen/sample gen/int 5)
  (gen/sample (gen/list gen/boolean))
  (gen/sample (gen/map gen/keyword gen/int))
  (gen/sample (gen/tuple gen/boolean gen/nat) 5)
  ;;such-that
  (gen/sample (gen/such-that not-empty (gen/list gen/int)))
  (gen/sample (gen/such-that even? gen/int))
  ;;fmap
  (gen/sample (gen/fmap set (gen/vector gen/nat)))
  (gen/sample (gen/elements [:a :b :c]))
  (gen/sample (gen/choose 5 9))
  (gen/sample (gen/one-of [gen/int (gen/return nil)]))
  (gen/sample (gen/frequency [[7 gen/int] [3 gen/char]]) 10))


(def sort-idempotent-prop
  (prop/for-all [v (gen/vector gen/int)]
    (= (sort v) (sort (sort v)))))

(println
  (tc/quick-check 100 sort-idempotent-prop))

(def prop-sorted-first-less-than-last
  (prop/for-all [v (gen/not-empty (gen/vector gen/int))]
    (let [s (sort v)]
      (< (first s) (last s)))))

(println
  (tc/quick-check 100 prop-sorted-first-less-than-last))

(def prop-no-42
  (prop/for-all [v (gen/vector gen/int)]
    (not (some #{42} v))))


(println
  (tc/quick-check 100 prop-no-42))


