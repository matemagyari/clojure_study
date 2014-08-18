(ns clojure-study.libraries.pattern-matching
  (:require [clojure.core.match :as m]
            [clojure-study.assertion :as a]))


;;============ simple matching =======================
(a/assert-equals :a
  (m/match 3
    3 :a
    4 :b))
;;============ vector matching =======================
(a/assert-equals ["Buzz" 11 "Fizz" 13 14 "FizzBuzz" 16]
  (for [n (range 10 17)]
    (m/match [(mod n 3) (mod n 5)]
      [0 0] "FizzBuzz"
      [0 _] "Fizz"
      [_ 0] "Buzz"
      :else n)))

;;destructuring
(a/assert-equals [4 5]
  (m/match [ [3 4 5] ]
    [[1 & r]] :a
    [[3 & r]] r))

;list doesn't match to vector
(a/assert-equals :else
  (m/match [ '(1 2 3) ]
    [[1 2 3]] :a
    [_] :else))

(a/assert-equals :else
  (m/match '(1 2 3)
    [[1 2 3]] :a
    ([1 & r] :seq) :else))

(a/assert-equals :a
  (m/match [ '(1 2 3) ]
    [([1 2 3] :seq)] :a
    [([2 2 2] :seq)] :b))

(a/assert-equals :a
  (m/match [ [1 2 3] ]
    [([1 2 3] :seq)] :a
    [([2 2 2] :seq)] :b))

(a/assert-equals []
  (m/match [[1 2]]
    [[1 2 & r]] r
    [_] :b))
;;============ OR matching =======================
(a/assert-equals :b
  (m/match [3 4 5]
    [3 (:or 6 7) 5] :a
    [3 (:or 3 4) 5] :b))

;;============ GUARDS matching =======================
(a/assert-equals :a
  (m/match [1 2 3]
    [_ (a :guard even?) _] :a
    [_ (a :guard odd?) _] :b))

;multiple guards
(a/assert-equals :c
  (m/match [1 2 3]
    [_ (a :guard [even? neg?]) _] :a
    [_ (a :guard odd?) _] :b
    :else :c))

(let [match-fn #(m/match %
                  {:address {:street _
                             :number _}} "an-address"
                  {:man {:weight _
                              :height _}} "a-man"
                  :else "no-idea")]
  (a/assert-equals "an-address" (match-fn {:address {:street "X"
                                                     :number 1}}))
  (a/assert-equals "a-man" (match-fn {:man {:weight 100
                                                 :height 190}}))
  (a/assert-equals "no-idea" (match-fn "???")))

;;============ wildcards = bindings =======================
(a/assert-equals 3 (m/match [1 2 3]
                     [1 2 x] x))

(a/assert-equals [1 3] (m/match [1 2 3]
                     [a 2 b] [a b]))

;====== play
(defn is-sorted [xs gt] (m/match [xs]
                          [([] :seq)] true
                          [([a] :seq)] true
                          [([a b & r] :seq)] (and (gt a b) (is-sorted (cons b r) gt))))

(a/assert-equals true (is-sorted [] <))
(a/assert-equals true (is-sorted [2] <))
(a/assert-equals true (is-sorted [2 3] <))
(a/assert-equals false (is-sorted [3 2] <))
(a/assert-equals true (is-sorted [2 4 9] <))
(a/assert-equals false (is-sorted [5 4 9] <))
(a/assert-equals false (is-sorted [5 9 8] <))

