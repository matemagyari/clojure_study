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
(assert-equals (my-flatten [[ 1 2]]) '(1 2))

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
                    (conj acc (first the-rest))) ]
        (recur new-acc (rest the-rest) )
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

(comment

;;my-comp
(defn my-comp [ & fns]
  (loop [acc [(last fns)]
         rest-fns (drop-last fns)]
    (println (apply str acc))
    (if (empty? rest-fns)
      #(acc %)
      (recur #(partial ((last rest-fns) acc) %);;((last rest-fns) acc);;(partial apply (last fns) acc)
             (drop-last rest-fns))))
  )

(assert-equals [3 2 1] ((my-comp rest reverse) [1 2 3 4 5]))
)
