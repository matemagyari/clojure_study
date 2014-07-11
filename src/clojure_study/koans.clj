(ns clojure_study.koans
  (:require clojure.contrib.core)
  (:use clojure-study.assertion))

;;------------------------------------------------------------------------------- KOANS -------------------------------------------

(defn my-count
  ([sequ] (my-count sequ 0))
  ([sequ len] (if (empty? sequ) len (recur (rest sequ) (inc len)))))

(assert-equals 3 (my-count [1 5 3]))

;;redefined using loop recur
(defn my-count [sequ]
  (loop [sequ sequ
         len 0]
    (if (empty? sequ)
      len
      (recur (rest sequ) (inc len)))))

(assert-equals 4 (my-count [1 5 3 4]))

(defn my-reverse [sequ]
  (loop [original sequ
         reversed '()]
    (if (empty? original)
      reversed
      (recur (rest original) (conj reversed (first original)))
      )))

(assert-equals [1 2 3] (my-reverse [3 2 1]))

;;fibonacci

(defn my-fib-num [n]
  (cond
    (< n 2) 0
    (= n 2) 1
    :else (+ (my-fib-num (- n 1))
            (my-fib-num (- n 2)))
    ))

(assert-equals (my-fib-num 0) 0)
(assert-equals (my-fib-num 1) 0)
(assert-equals (my-fib-num 2) 1)
(assert-equals (my-fib-num 3) 1)
(assert-equals (my-fib-num 4) 2)
(assert-equals (my-fib-num 5) 3)
(assert-equals (my-fib-num 6) 5)

(defn my-fib [n]
  (loop [result [1 1]
         len n]
    (if (> 3 len)
      result
      (let [last (last result)
            ;lastbut (first (rest (reverse result)))
            lastbut (->> result reverse rest first)
            next (+ last lastbut)]
        (recur (conj result next)
          (dec len))))))

(assert-equals (my-fib 7) [1 1 2 3 5 8 13])

;;palindrome

(defn is-palindrome [a-seq]
  (loop [x a-seq]
    (cond
      (> 2 (count x)) true
      (not= (first x) (last x)) false
      :else (recur (->> x rest reverse rest reverse))
      )))

(assert-false (is-palindrome '(1 2 3)))
(assert (is-palindrome '(1 1)))
(assert (is-palindrome '(1 2 3 2 1)))
(assert (is-palindrome '(:a :b :a)))
(assert (is-palindrome "racecar"))

;;flatten a sequence
(defn my-flatten [a-seq]
  (if (sequential? a-seq)
    (mapcat my-flatten a-seq)
    (list a-seq)))

(assert-equals (my-flatten '((1 2) 3 [4 [5 6]])) '(1 2 3 4 5 6))
(assert-equals (my-flatten [[1 2]]) '(1 2))

;;max
(defn my-max [& args]
  (loop [maxx (first args)
         rest-args (rest args)]
    (if (empty? rest-args)
      maxx
      (let [second (first rest-args)
            local-max (if (> second maxx) second maxx)
            other (rest rest-args)]
        (recur local-max other)))))

(assert-equals 8 (my-max 1 8 3 4))

;;interleave
(defn my-interleave [a-seq b-seq]
  (loop [result []
         a-rem a-seq
         b-rem b-seq]
    (cond
      (empty? a-rem) result
      (empty? b-rem) result
      :else (recur
              (conj result (first a-rem) (first b-rem))
              (rest a-rem)
              (rest b-rem)))))

(assert-equals (my-interleave [1 2] [3 4 5 6]) '(1 3 2 4))
(assert-equals (my-interleave [1 2 3] [:a :b :c]) '(1 :a 2 :b 3 :c))

;;range

(defn my-range [start end]
  (loop [acc []
         index start]
    (if (= index end)
      acc
      (recur (conj acc index) (inc index)))))

(assert-equals (my-range -2 2) '(-2 -1 0 1))

;;replicate sequence

(defn my-replicate [a-seq n]
  (loop [acc []
         rest-seq a-seq]
    (if (empty? rest-seq)
      acc
      (let [rep (repeat n (first rest-seq))]
        (recur (concat acc rep)
          (rest rest-seq))))))

(assert-equals (my-replicate [44 33] 2) [44 44 33 33])

;; interpose

(defn my-interpose [delimiter a-seq]
  (loop [acc [(first a-seq)]
         rest-seq (rest a-seq)]
    (if (empty? rest-seq)
      acc
      (recur (conj acc delimiter (first rest-seq))
        (rest rest-seq))
      )))

(assert-equals (my-interpose 0 [1 2 3]) [1 0 2 0 3])

;;last
(defn my-last [a-seq]
  (loop [acc a-seq]
    (if (= 1 (count acc))
      (first acc)
      (recur (rest acc)))))

(assert-equals (my-last [1 2 3]) 3)

;;get the caps
(defn get-caps [text]
  (->> text
    (filter #(java.lang.Character/isUpperCase %))
    (apply str)))

(assert-equals (get-caps "HeLlO, WoRlD!") "HLOWRD")

;;compress a seq
(defn my-comp [a-seq]
  (loop [acc [(first a-seq)]
         the-rest (rest a-seq)]
    (if (empty? the-rest)
      acc
      (let [next-char (first the-rest)
            new-acc (if (= (last acc) next-char)
                      acc
                      (conj acc (first the-rest)))]
        (recur new-acc (rest the-rest))
        ))))


(assert-equals (apply str (my-comp "Leeeeeerrroyyy")) "Leroy")

;;pack a seq

(defn pack [a-seq]
  (loop [act-acc [(first a-seq)]
         acc []
         the-rest (rest a-seq)]
    (if (empty? the-rest)
      (conj acc act-acc)
      (let [next-char (first the-rest)
            same-char-coming (= (last act-acc) next-char)
            new-act-acc (if same-char-coming
                          (conj act-acc next-char)
                          [next-char])
            new-acc (if same-char-coming
                      acc
                      (conj acc act-acc))]
        (recur new-act-acc
          new-acc
          (rest the-rest))))))

(assert-equals (pack [:a :a :b :b :c]) '((:a :a) (:b :b) (:c)))

;factorial
(defn my-fact [x]
  (if (zero? x) 1
    (* x (my-fact (dec x)))))

(defn my-fact2 [x]
  (loop [a x
         res 1]
    (if (= 1 a) res
      (recur (dec a) (* a res)))))

(assert-equals 6 (my-fact2 3))
(assert-equals 120 (my-fact2 5))

;;gcd
(comment

  (defn gcd [a b]
    (cond
      (zero? (mod a b)) b
      (zero? (mod b a)) a
      :else))

  )

;;occurences

(defn occ [a-seq]
  (loop [s a-seq
         o {}]
    (if (empty? s) o
      (let [f (first s)
            r (rest s)
            v (get o f 0)
            o (assoc o f (inc v))]
        (recur r o)))))

(assert-equals (occ "abacbcccb") {\a 2 \b 3 \c 4})

;;hex->dec
(use 'clojure.contrib.math)

(defn convert [original convert-char base]
  (loop [acc 0
         char-seq original
         pos 0]
    (if (empty? char-seq) acc
      (let [last-char (-> char-seq last str)
            remainder (-> char-seq reverse rest reverse)
            pos-val (convert-char last-char)
            pos-pow (expt base pos)
            val (* pos-val pos-pow)]
        (recur (+ acc val) remainder (inc pos))))))

(defn hex->dec [a-hex]
  (let [to-val (fn [x]
                 (cond
                   (= "A" x) 10
                   (= "B" x) 11
                   (= "C" x) 12
                   (= "D" x) 13
                   (= "E" x) 14
                   (= "F" x) 15
                   :else (read-string x)))]
    (convert a-hex to-val 16)))

(assert-equals (hex->dec "10") 16)
(assert-equals (hex->dec "A0") 160)
(assert-equals (hex->dec "AA") 170)
(assert-equals (hex->dec "ABC") 2748)
(assert-equals (hex->dec "ABCD") 43981)
(assert-equals (hex->dec "AF45D") 717917)

;anagram finder
(defn anagrams [the-words]
  (->> the-words
    (group-by sort)
    vals
    (filter #(> (count %) 1))
    (map set)
    set))

;flipping out
(defn flip [f]
  (fn [x1 x2] (f x2 x1)))

;rotate sequence
(defn rotate [shift a-seq]
  (let [len (count a-seq)
        shift (mod shift len)
        [seq1 seq2] (split-at shift a-seq)]
    (take len (concat seq2 seq1))))

(assert-equals (rotate 2 [1 2 3 4]) [3 4 1 2])

;reverse interleave
(defn rev-i [a-seq n]
  (let [len (count a-seq)
        groups (group-by #(mod % n) a-seq)]
    (vals groups)))

(assert-equals (rev-i (range 9) 3) [[0 3 6] [1 4 7] [2 5 8]])

;split-by-type
(defn splitter [a-seq]
  (->> a-seq (group-by type) vals))

(assert-equals (set (splitter [1 :a 2 :b 3 :c])) #{[1 2 3] [:a :b :c]})

;distinct
(defn my-distinct [a-seq]
  (loop [acc []
         s a-seq]
    (if (empty? s) acc
      (let [f (first s)
            acc2 (if (.contains acc f) acc
                   (conj acc f))]
        (recur acc2 (rest s))))))

(assert-equals (my-distinct [1 2 1 3 1 2 4]) [1 2 3 4])

;word sorting
(use '[clojure.string :only (split triml upper-case join)])

(defn word-sorter [sentence]
  (let [pun-f #(not (.contains [\. \!] %))
        sentence (join (filter pun-f sentence))
        a-seq (split sentence #"\s")
        comparator #(compare (upper-case %1) (upper-case %2))]
    (sort comparator a-seq)))

(assert-equals (word-sorter "Have a nice day.")
  ["a" "day" "Have" "nice"])

;;my-comp
(defn my-comp [& fns]
  (fn [& x]
    (let [temp-result (apply (last fns) x)]
      (loop [temp-fns (drop-last fns)
             temp-result temp-result]
        (if (empty? temp-fns) temp-result
          (let [actual-fn (last temp-fns)
                rest-fns (drop-last temp-fns)]
            (recur rest-fns (actual-fn temp-result))))))))

(assert-equals ((my-comp rest reverse) [1 2 3 4 5]) [4 3 2 1])
(assert-equals true ((my-comp zero? #(mod % 8) +) 3 5 7 9))

;my-juxt
(defn my-juxt [& fns] (fn [& x] (for [a-fn fns] (apply a-fn x))))

(assert-equals [21 6 1] ((my-juxt + max min) 2 3 5 1 6 4))

;my-part
(defn my-part [n a-range]
  (loop [rest-seq a-range
         acc []]
    (if (> n (count rest-seq)) acc
      (let [a (take n rest-seq)
            new-acc (conj acc a)
            c (drop n rest-seq)]
        (recur c new-acc)))))

(assert-equals (my-part 3 (range 8)) '((0 1 2) (3 4 5)))

;search common min
(defn common-min [& seqs]
  (let [fs (first seqs)
        rs (rest seqs)
        mins-at-least (fn [x seqs2]
                        (for [s seqs2]
                          (last (take-while #(>= x %) s))))
        is-the-one? (fn [x seq2]
                      (= (mins-at-least x seq2)
                        (for [s seq2] x)))]
    (loop [possible-mins fs]
      (if (empty? possible-mins)
        nil
        (if (is-the-one? (first possible-mins) rs)
          (first possible-mins)
          (recur (rest possible-mins)))))))

(assert-equals (common-min [1 2 3 4 5 6 7] [0.5 3/2 4 19]) 4)
(assert-equals (common-min [1 2 3]) 1)

;;67. prime numbers
(defn first-n-primes [n]
  (let [find-dividers (fn [x]
                        (let [one-to-x (take x (iterate inc 1))]
                          (filter #(zero? (mod x %)) one-to-x)))
        prime? (fn [x] (= 2 (count (find-dividers x))))]
    (loop [primes []
           i 1]
      (if (= n (count primes)) primes
        (let [np (if (prime? i)
                   (conj primes i)
                   primes)]
          (recur np (inc i)))))))

(assert-equals (first-n-primes 5) [2 3 5 7 11])
(assert-equals (first-n-primes 1) [2])

