(ns FirstClojure.core 
  (:require clojure.contrib.core))

;;=========================
(defn assert-equals [actual expected]
  (when-not (= actual expected)
    (throw 
      (AssertionError. 
        (str "Expected " expected " but was " actual)))))

(defn assert-false [expr] (assert (not expr)))    

;;------------------------------------------------------------------------------- BASICS -------------------------------------------
(defn do-sth []
  (println "hello")
  (println "bello"))

;;overloading
(defn fake 
  ([] 0)
  ([x] x)
  ([x y] (+ x y))
  ([x y & z] (+ x y (reduce * z))))

(assert-equals (fake) 0)
(assert-equals (fake 5) 5)
(assert-equals (fake 5 8) 13)
(assert-equals (fake 2 3 4 5 6) 125)

;;------------------------------------------------------------------------------- FUNCTIONS -------------------------------------------

;;conj
(assert-equals (conj [1 2] 3) [1 2 3])

;;compose
(defn compose-fn [nums]
  (map (comp #(+ 1 %) 
             #(* 2 %) )
       (filter even? nums))
  )
(assert-equals (compose-fn [1 2 3 4]) [5 9])

(def comp-fn (comp #(+ 1 %) #(* 2 %)))
(assert-equals (comp-fn 3) 7)

;;complement
(assert-equals ((complement zero?) 1) true)


;;partial
(defn calculate-net [tax gross-amount] (* (- 100 tax) 0.01 gross-amount))
(def calculate-net-ny (partial calculate-net 10)) 
(def calculate-net-sf (partial calculate-net 15)) 

(defn add [a b] (+ a b))
(def add2 (partial add 2))
(assert-equals (add2 3) 5)

;;(defn tax-ny [amount] (partial #()) )

;;------------------------------------------------------------------------------- RECUR -------------------------------------------

(defn my-nth [sequ n] 
  (if (= n 0) 
    (first sequ) 
    (recur (rest sequ) (dec n))))

(assert-equals 5 (my-nth [1 5 3] 1))

;;------------------------------------------------------------------------------- USEFUL MACROS -------------------------------------------


;;->
(assert-equals 5/2
               (-> 5
                 (/ 2)))
(assert-equals 2/5
               (->> 5
                 (/ 2)))
(assert-equals 15
               (->> [1 2 3 4 5 6]
                 (filter even?)
                 (map inc)
                 (apply +)))


(use '[clojure.contrib.core :only (-?>>)])
;(require 'clojure.contrib.core)

;(assert-equals nil
;               (-?>> [1 3 5]
;                 (filter even?)
;                 (map inc)
;                 (apply +)))

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


;;------------------------------------------------------------------------------- MAPS -------------------------------------------
(def john {:name "John" :age 17}) 
(def jack {:name "Jack" :age 31})

; keys functions of maps and vice-versa
(assert-equals (:name john) "John")
(assert-equals (:name john) (john :name))

;;change map
(assert-equals (assoc jack :gender "M" :title "Mr")
               {:name "Jack" :age 31 :gender "M" :title "Mr"}) 
(assert-equals (dissoc jack :age)
               {:name "Jack"})
(assert-equals (select-keys {:a 1 :b 2 :c 3} [:a :c]) {:a 1 :c 3})
(assert-equals (merge {:a 1 :b 2} {:c 3} {:a 4 :d 5})
               {:a 4 :b 2 :c 3 :d 5})
;;(assert-equals (merge-with #(+ 10 %) {:a 1 :b 2} {:c 3} {:a 4 :d 5})
;;               {:a 4 :b 2 :c 3 :d 5})

;;------------------------------------------------------------------------------- SETS -------------------------------------------
(assert-equals #{:a :b :c} #{:b :c :a} )


; elements functions of sets and vice-versa
(def myset #{:a :b :c})
(assert-equals (:a myset) :a)

(assert-equals (disj #{1 2 3 4} 2 3) #{1 4}) 

;;------------------------------------------------------------------------------- VECTORS -------------------------------------------
(assert-equals (assoc [1 2] 0 333)
               [333 2]) 
(assert-equals (assoc [1 2] 2 333)
               [1 2 333]) 
;;create vector
(assert-equals (vector :a :b) [:a :b]) 
;;examine vector
(assert (vector? [1 2])) 
(assert-false (vector? :a)) 
(assert-equals (nth [1 2] 1) 2) 
(assert-equals (get [1 2] 1) 2) 
(assert-equals (get [1 2] 5 6) 6) 
(assert-equals (last [1 2]) 2) 
(assert-equals (peek [1 2]) 2) 
(assert-equals (first [1 2]) 1)
;;change a vector
(assert-equals (pop [1 2 3]) [1 2]) 
(assert-equals (subvec [1 2 3 4] 1 3) [2 3])
(assert-equals (replace {:a :aa :c :cc} [:a :b :c]) [:aa :b :cc])

;;------------------------------------------------------------------------------- LISTS -------------------------------------------
(assert-equals (list 1 2 3) '(1 2 3))
(assert-equals 2 (peek '(2 3)))
(assert (list? '(1 2)))

;;filter
(defn filter-minors [persons] (filter #(> 18 (:age %)) persons))

(assert-equals (filter-minors [john jack]) [john])

;;------------------------------------------------------------------------------- FUTURE -------------------------------------------

(def delayed-1 (future (java.lang.Thread/sleep 100) 1))
(assert-false (realized? delayed-1))
(assert-false (future-done? delayed-1))
(java.lang.Thread/sleep 100)
(assert (realized? delayed-1))
(assert-equals @delayed-1 1)

(def delayed-2 (future (java.lang.Thread/sleep 100) 2))
(assert-false (future-cancelled? delayed-2))
(future-cancel delayed-2)
(assert (future-cancelled? delayed-2))

;;------------------------------------------------------------------------------- PROMISE -------------------------------------------
(def x (promise))
(deliver x 12)
(assert-equals @x 12)

(def guest-count (promise))
(defn manager-duties [cnt] 
  (println "manager waiting...")
  (println (str " hi " @cnt " arrived")))
(future (manager-duties guest-count))
(println "..count almost delivered")
(deliver guest-count 50)
(assert (realized? guest-count))
   
;;------------------------------------------------------------------------------- REFS -------------------------------------------
(def location (ref "London"))
(def salary (ref 100))
(dosync 
  (ref-set location "NY")
  (alter salary #(+ 50 %)))

(assert-equals @location "NY")
(assert-equals @salary 150)

(def location (ref "London"))
(def salary (ref 100))
(defn doit []
  (dosync 
    (ref-set location "NY")
    (println (str "Location " @location))
    (throw (RuntimeException.))
    (println (str "Location " @location))))

(try (doit)
  (catch Exception ex (println (str "caught ex" (.getMessage ex)))))

(assert-equals @location "London")

;;------------------------------------------------------------------------------- ATOMS -------------------------------------------
(def counter (atom 0))

(swap! counter #(+ 2 %))
(assert-equals 2 @counter)

(defn my-add [& args] (apply + args))
(assert-equals 5 (my-add 2 2 1)) 

(swap! counter my-add 3 4)
(assert-equals 9 @counter)
(reset! counter 14)
(assert-equals 14 @counter)

;;------------------------------------------------------------------------------- VALIDATORS -----------------------------------------

(def binary (atom 0 :validator (fn [new-val] (or (== 0 new-val) (== 1 new-val)))))
(try (reset! binary 2) (catch IllegalStateException ex))
(assert-equals 0 @binary)

(def a-name (atom "Bela" :validator string?))
(try (reset! a-name 2) (catch IllegalStateException ex (println "Not a string")))
(assert-equals "Bela" @a-name)

(def a-positive (atom 1 :validator #(> % 0)))
(try (reset! a-positive -1) (catch IllegalStateException ex (println "Not positive")))
(assert-equals 1 @a-positive)


(defn my-error-handler [agent, exc] 
  (println (str "Whoops " agent " " exc)))

(def a-negative (agent -1))
(set-validator! a-negative neg?)
(set-error-handler! a-negative my-error-handler)

(try (send a-negative (partial + 1)) (catch IllegalStateException ex))
(assert-equals -1 @a-negative)


;;------------------------------------------------------------------------------- WATCHERS -----------------------------------------

(def watched-val (atom 0))
(add-watch watched-val :watch-change (fn [key a old-val new-val] (println (str key " " a " " old-val " " new-val))))
(reset! watched-val 5)
(remove-watch watched-val :watch-change)
;;------------------------------------------------------------------------------- LAZY-SEQ -------------------------------------------
(def integers (range 100000))
(assert-equals (take 3 integers) [0 1 2])

(defn positive-numbers
    ([] (positive-numbers 1))
    ([n] (cons n (lazy-seq (positive-numbers (inc n))))))

(assert-equals (take 3 (positive-numbers)) [1 2 3])

;;------------------------------------------------------------------------------- PRE POST -------------------------------------------
(defn constrained-fn [f x]
  {:pre [(pos? x) (even? x)]
   :post [(> 10 %)]}
  (f x))

(try (constrained-fn #(+ 5 %) 1) (catch AssertionError err (println "Not even")))
(try (constrained-fn #(+ 5 %) -1) (catch AssertionError err (println "Not positive")) )
(try (constrained-fn #(* 2 %) 6) (catch AssertionError err (println "Greater than 10")) )
