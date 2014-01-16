(ns FirstClojure.core)

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
(defn manager-duties [cnt] (println (str " hi " cnt " arrived")))
(future (manager-duties guest-count))
(deliver guest-count 50)
(if (realized? guest-count) (deref guest-count))
   
;;------------------------------------------------------------------------------- DOSYNC -------------------------------------------
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

;;------------------------------------------------------------------------------- LAZY-SEQ -------------------------------------------
(def integers (range 100000))
(assert-equals (take 3 integers) [0 1 2])

(defn positive-numbers
    ([] (positive-numbers 1))
    ([n] (cons n (lazy-seq (positive-numbers (inc n))))))

(assert-equals (take 3 (positive-numbers)) [1 2 3])
