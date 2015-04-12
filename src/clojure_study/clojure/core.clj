(ns clojure-study.clojure.core
  (:require clojure.contrib.core)
  (:use clojure-study.clojure.assertion))

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

(assert= (fake) 0)
(assert= (fake 5) 5)
(assert= (fake 5 8) 13)
(assert= (fake 2 3 4 5 6) 125)

;;------------------------------------------------------------------------------- NIL is nothing -------------------------------------------
(assert= nil (first nil))
(assert= [] (rest nil))
;;------------------------------------------------------------------------------- DECONSTRUCTING -------------------------------------------
;;vector
(let [[x y] [1 2]]
  (assert= x 1)
  (assert= y 2))

(let [[x & more] [1 2 3]]
  (assert= x 1)
  (assert= more [2 3]))

;;map
(let [a-map {:weight 100 :height 180}
      {w :weight h :height} a-map]
  (assert= w 100)
  (assert= h 180))

(let [a-map {:p 1 :q 2}
      {:keys [p q z]} a-map]
  (assert= 1 p)
  (assert= 2 q)
  (assert= nil z))

;;------------------------------------------------------------------------------- FUNCTIONS -------------------------------------------

;;repeatedly
(def x (repeatedly #(rand-int 10)))
(println (take 3 x))

;;iterate
(assert= [1 2 3] (take 3 (iterate inc 1)))

;;conj
(assert= (conj [1 2] 3) [1 2 3])

;;compose
(defn compose-fn [nums]
  (map (comp #(+ 1 %)
         #(* 2 %))
    (filter even? nums))
  )
(assert= (compose-fn [1 2 3 4]) [5 9])

(def comp-fn (comp #(+ 1 %) #(* 2 %)))
(assert= (comp-fn 3) 7)

;;complement
(assert= ((complement zero?) 1) true)

;;partial
(defn calculate-net [tax gross-amount] (* (- 100 tax) 0.01 gross-amount))
(def calculate-net-ny (partial calculate-net 10))
(def calculate-net-sf (partial calculate-net 15))

(defn add [a b] (+ a b))
(defn dup [a] (* a 2))
(def add2 (partial add 2))
(assert= (add2 3) 5)

(def my-add (partial +))
(assert= (my-add 3 2) 5)


(def my-dup (partial * 2))
(assert= (my-dup 5) 10)

(def add-and-dup (partial my-dup my-add))
;;(assert-equals (add-and-dup 5 3) 16)


;;(defn tax-ny [amount] (partial #()) )

;;compose

(defn comp2 [f g]
  (fn [& args]
    (f (apply g args))))


(assert= ((comp2 dup add) 5 8) 26)
(comment
  (defn comp-n [& fns]
    (loop [acc (fn [& args] (apply (first fns) args))
           rest-fns (rest fns)]
      (if (empty rest-fns)
        acc
        (recur ((fn [& args]
                  ((first rest-fns) acc)))
          (rest rest-fns)))))

  (assert= ((comp-n dup add) 5 8) 26)
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

(assert= true (trampoline my-even? 1000000))
(assert= false (trampoline my-even? 1000001))

;;frequency
(assert= {:a 2 :b 3 :c 1} (frequencies [:a :b :c :a :b :b]))
(time
  (frequencies (range 1000)))

;;------------------------------------------------------------------------------- RECUR -------------------------------------------

(defn my-nth [sequ n]
  (if (= n 0)
    (first sequ)
    (recur (rest sequ) (dec n))))

(assert= 5 (my-nth [1 5 3] 1))

;;------------------------------------------------------------------------------- SPECIAL FORMS -------------------------------------------
;;letfn
(assert= 18 (letfn [(twice [x] (* 2 x))
                          (plus-1 [x] (inc x))]
                    (twice
                      (plus-1 8))))

;;------------------------------------------------------------------------------- USEFUL MACROS -------------------------------------------


;;->
(assert= 5/2
  (-> 5
    (/ 2)))
(assert= 2/5
  (->> 5
    (/ 2)))
(assert= 15
  (->> [1 2 3 4 5 6]
    (filter even?)
    (map inc)
    (apply +)))


(use '[clojure.contrib.core :only (-?>>)])

(assert= 0
  (-?>> [1 3 5]
    (filter even?)
    (map inc)
    (apply +)))

;;------------------------------------------------------------------------------- SETS -------------------------------------------
(assert= #{:a :b :c} #{:b :c :a})


; elements functions of sets and vice-versa
(def myset #{:a :b :c})
(assert= (:a myset) :a)

(assert= (disj #{1 2 3 4} 2 3) #{1 4})

;;------------------------------------------------------------------------------- VECTORS -------------------------------------------
(assert= (assoc [1 2] 0 333)
  [333 2])
(assert= (assoc [1 2] 2 333)
  [1 2 333])
;;create vector
(assert= (vector :a :b) [:a :b])
;;examine vector
(assert (vector? [1 2]))
(assert-false (vector? :a))
(assert= (nth [1 2] 1) 2)
(assert= (get [1 2] 1) 2)
(assert= (get [1 2] 5 6) 6)
(assert= (last [1 2]) 2)
(assert= (peek [1 2]) 2)
(assert= (first [1 2]) 1)
;;change a vector
(assert= (pop [1 2 3]) [1 2])
(assert= (subvec [1 2 3 4] 1 3) [2 3])
(assert= (replace {:a :aa :c :cc} [:a :b :c]) [:aa :b :cc])

;;------------------------------------------------------------------------------- LISTS -------------------------------------------
(assert= (list 1 2 3) '(1 2 3))
(assert= 2 (peek '(2 3)))
(assert (list? '(1 2)))

;;filter
(defn filter-minors [persons] (filter #(> 18 (:age %)) persons))
(def john {:name "John" :age 17})
(def jack {:name "Jack" :age 27})

(assert= (filter-minors [john jack]) [john])

;;------------------------------------------------------------------------------- FUTURE -------------------------------------------

(def delayed-1 (future (java.lang.Thread/sleep 100) 1))
(assert-false (realized? delayed-1))
(assert-false (future-done? delayed-1))
(java.lang.Thread/sleep 100)
(assert (realized? delayed-1))
(assert= @delayed-1 1)

(def delayed-2 (future (java.lang.Thread/sleep 100) 2))
(assert-false (future-cancelled? delayed-2))
(future-cancel delayed-2)
(assert (future-cancelled? delayed-2))

;;------------------------------------------------------------------------------- PROMISE -------------------------------------------
(def x (promise))
(deliver x 12)
(assert= @x 12)

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
(assert= (take 3 integers) [0 1 2])

(defn positive-numbers
  ([] (positive-numbers 1))
  ([n] (cons n (lazy-seq (positive-numbers (inc n))))))

(assert= (take 3 (positive-numbers)) [1 2 3])

;;------------------------------------------------------------------------------- PRE POST -------------------------------------------
(defn constrained-fn [f x]
  {:pre [(pos? x) (even? x)]
   :post [(> 10 %)]}
  (f x))

(try (constrained-fn #(+ 5 %) 1) (catch AssertionError err (println "Not even")))
(try (constrained-fn #(+ 5 %) -1) (catch AssertionError err (println "Not positive")))
(try (constrained-fn #(* 2 %) 6) (catch AssertionError err (println "Greater than 10")))


;;------------------------------------------------------------------------------- FOR COMPREHENSIONS  -------------------------------------------
(assert= [[:a :c] [:a :d] [:b :c] [:b :d]]
  (for [x [:a :b]
        y [:c :d]]
    [x y]))

(assert= [0 6 12]
  (for [x '(0 1 2 3 4 5)
        :let [y (* x 3)]
        :when (even? y)]
    y))

(assert= [3 4 5]
  (for [x '(1 2 3)
        :let [y (inc x)
              z (inc y)]]
    z))

