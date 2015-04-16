(ns
  ^{:author mate.magyari}
  clojure-study.clojure.core-functions
  (:require [clojure-study.clojure.assertion :as a]))

;; Examples for the most important built-in functions

;;====== conj ======
(a/assert=
  [1 2 3 4]
  (conj [1 2] 3 4)) ;vector

(a/assert=
  [4 3 1 2]
  (conj '(1 2) 3 4)) ;list

;;====== cons ======
(a/assert=
  [3 1 2]
  (cons 3 [1 2])) ;vector

(a/assert=
  [3 1 2]
  (cons 3 '(1 2))) ;list

;; mapcat
(a/assert=
  [2 1 4 3]
  (mapcat reverse [[1 2] [3 4]]))

;;comp
(a/assert= 19 (let [a-fn (comp
                           inc
                           #(* 2 %)
                           dec)]
                (a-fn 10)))

;;into
(a/assert=
  {:a 1 :b 2}
  (into {} [[:a 1] [:b 2]]))

(a/assert=
    #{[:a 1] [:b 2]}
  (into #{} [[:a 1] [:b 2]]))

(a/assert=
  {:a 1 :b 7 :c 3}
  (into (sorted-map) {:b 7 :c 3 :a 1}))

;;fnil
(let [divide (fn [a b] (/ a b))
      safe-divide (fnil divide 10 2)]
  (a/assert= 7 (safe-divide 21 3))
  (a/assert= 21/2 (safe-divide 21 nil))
  (a/assert= 10/5 (safe-divide nil 5))
  (a/assert= 10/2 (safe-divide nil nil)))

;;juxt
(a/assert= [3 1 true false] ((juxt inc dec even? odd?) 2))

;;peek and pop
(a/assert= 3 (peek [1 2 3]))
(a/assert= 1 (peek '(1 2 3)))

(a/assert= [1 2] (pop [1 2 3]))
(a/assert= '(2 3) (pop '(1 2 3)))


(let [check-coll (fn [coll]
                   (let [coll-1 (conj coll 999)
                         coll-2 (pop coll-1)]
                     (a/assert= 999 (peek coll-1))
                     (a/assert= coll coll-2)))]
  (do
    (check-coll [1 2 3])
    (check-coll '(1 2 3))))


;;repeatedly
(defn one [] 1)
(a/assert= [1 1] (take 2
                   (repeatedly 2 one)))

;;iterate
(a/assert= [1 2 3] (take 3 (iterate inc 1)))

;;complement
(a/assert= ((complement zero?) 1) true)

;;trampoline
(declare my-odd?)
(defn my-even? [n]
  (if (zero? n)
    true
    #(my-odd? (dec n))))

(defn my-odd? [n]
  (if (= 1 n)
    true
    #(my-even? (dec n))))

;(a/assert= true (trampoline my-even? 10))
;(a/assert= false (trampoline my-even? 11))

;;frequency
(a/assert= {:a 2 :b 3 :c 1} (frequencies [:a :b :c :a :b :b]))

;;replace
(a/assert= [:one :two 3] (replace {1 :one 2 :two} [1 2 3]))

;;unzip
;(zipmap)
