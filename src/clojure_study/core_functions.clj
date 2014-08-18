(ns
  ^{:author mate.magyari}
  clojure-study.core-functions
  (:require [clojure-study.assertion :as a]))

;;====== conj ======
(a/assert-equals
  [1 2 3 4]
  (conj [1 2] 3 4)) ;vector

(a/assert-equals
  [4 3 1 2]
  (conj '(1 2) 3 4)) ;list

;;====== cons ======
(a/assert-equals
  [3 1 2]
  (cons 3 [1 2])) ;vector

(a/assert-equals
  [3 1 2]
  (cons 3 '(1 2))) ;list

;; mapcat
(a/assert-equals
  [2 1 4 3]
  (mapcat reverse [[1 2] [3 4]]))

;;comp
(a/assert-equals 19 (let [a-fn (comp
                               inc
                               #(* 2 %)
                               dec)]
                    (a-fn 10)))

;;into
(a/assert-equals
  {:a 1 :b 2}
  (into {} [[:a 1] [:b 2]]))

(a/assert-equals
  #{[:a 1] [:b 2]}
  (into #{} [[:a 1] [:b 2]]))

(a/assert-equals
  {:a 1 :b 7 :c 3}
  (into (sorted-map) {:b 7 :c 3 :a 1}))

;;fnil
(let [divide (fn [a b] (/ a b))
      safe-divide (fnil divide 10 2)]
  (a/assert-equals 7 (safe-divide 21 3)
  (a/assert-equals 21/2 (safe-divide 21 nil))
  (a/assert-equals 10/5 (safe-divide nil 5))
  (a/assert-equals 10/2 (safe-divide nil nil))))

;;juxt
(a/assert-equals [1 3 true false] ((juxt inc dec even? odd?) 2))

;;peek and pop
(a/assert-equals 3 (peek [1 2 3]))
(a/assert-equals 1 (peek '(1 2 3)))

(a/assert-equals [1 2] (pop [1 2 3]))
(a/assert-equals '(2 3) (pop '(1 2 3)))


(let [check-coll (fn [coll]
                       (let [coll-1 (conj coll 999)
                             coll-2 (pop coll-1)]
                         (a/assert-equals 999 (peek coll-1))
                         (a/assert-equals coll coll-2)))]
  (do
    (check-coll [1 2 3])
    (check-coll '(1 2 3))))


;;unzip
;(zipmap)
