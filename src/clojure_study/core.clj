(ns clojure-study.core
  (:require clojure.contrib.core)
  (:use clojure-study.assertion))

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

;;------------------------------------------------------------------------------- NIL is nothing -------------------------------------------
(assert-equals nil (first nil))
(assert-equals [] (rest nil))
;;------------------------------------------------------------------------------- DECONSTRUCTING -------------------------------------------
;;vector
(let [[x y] [1 2]]
  (assert-equals x 1)
  (assert-equals y 2))

(let [[x & more] [1 2 3]]
  (assert-equals x 1)
  (assert-equals more [2 3]))

;;map
(let [a-map {:weight 100 :height 180}
      {w :weight h :height} a-map]
  (assert-equals w 100)
  (assert-equals h 180))

(let [a-map {:p 1 :q 2}
      {:keys [p q z]} a-map]
  (assert-equals 1 p)
  (assert-equals 2 q)
  (assert-equals nil z))

;;------------------------------------------------------------------------------- FUNCTIONS -------------------------------------------

;;repeatedly
(def x (repeatedly #(rand-int 10)))
(println (take 3 x))

;;iterate
(assert-equals [1 2 3] (take 3 (iterate inc 1)))

;;conj
(assert-equals (conj [1 2] 3) [1 2 3])

;;compose
(defn compose-fn [nums]
  (map (comp #(+ 1 %)
         #(* 2 %))
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
(defn dup [a] (* a 2))
(def add2 (partial add 2))
(assert-equals (add2 3) 5)

(def my-add (partial +))
(assert-equals (my-add 3 2) 5)


(def my-dup (partial * 2))
(assert-equals (my-dup 5) 10)

(def add-and-dup (partial my-dup my-add))
;;(assert-equals (add-and-dup 5 3) 16)


;;(defn tax-ny [amount] (partial #()) )

;;compose

(defn comp2 [f g]
  (fn [& args]
    (f (apply g args))))


(assert-equals ((comp2 dup add) 5 8) 26)
(comment
  (defn comp-n [& fns]
    (loop [acc (fn [& args] (apply (first fns) args))
           rest-fns (rest fns)]
      (if (empty rest-fns)
        acc
        (recur ((fn [& args]
                  ((first rest-fns) acc)))
          (rest rest-fns)))))

  (assert-equals ((comp-n dup add) 5 8) 26)
  )

;;trampoline
(defn my-even? [n]
  (if (zero? n)
    true
    #(my-odd? (dec n))))

(defn my-odd? [n]
  (if (= 1 n)
    true
    #(my-even? (dec n))))

(assert-equals true (trampoline my-even? 1000000))
(assert-equals false (trampoline my-even? 1000001))

;;frequency
(assert-equals {:a 2 :b 3 :c 1} (frequencies [:a :b :c :a :b :b]))
(time
  (frequencies (range 1000)))

;;------------------------------------------------------------------------------- RECUR -------------------------------------------

(defn my-nth [sequ n]
  (if (= n 0)
    (first sequ)
    (recur (rest sequ) (dec n))))

(assert-equals 5 (my-nth [1 5 3] 1))

;;------------------------------------------------------------------------------- SPECIAL FORMS -------------------------------------------
;;letfn
(assert-equals 18 (letfn [(twice [x] (* 2 x))
                          (plus-1 [x] (inc x))]
                    (twice
                      (plus-1 8))))

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

(assert-equals 0
  (-?>> [1 3 5]
    (filter even?)
    (map inc)
    (apply +)))

;;------------------------------------------------------------------------------- SETS -------------------------------------------
(assert-equals #{:a :b :c} #{:b :c :a})


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
(def john {:name "John" :age 17})
(def jack {:name "Jack" :age 27})

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
(try (constrained-fn #(+ 5 %) -1) (catch AssertionError err (println "Not positive")))
(try (constrained-fn #(* 2 %) 6) (catch AssertionError err (println "Greater than 10")))


;;------------------------------------------------------------------------------- FOR COMPREHENSIONS  -------------------------------------------
(assert-equals [[:a :c] [:a :d] [:b :c] [:b :d]]
  (for [x [:a :b]
        y [:c :d]]
    [x y]))

(assert-equals [0 6 12]
  (for [x '(0 1 2 3 4 5)
        :let [y (* x 3)]
        :when (even? y)]
    y))

(assert-equals [3 4 5]
  (for [x '(1 2 3)
        :let [y (inc x)
              z (inc y)]]
    z))

