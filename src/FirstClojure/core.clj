(ns FirstClojure.core)

;;=========================
(defn assert-equals [actual expected]
  (when-not (= actual expected)
    (throw 
      (AssertionError. 
        (str "Expected " expected " but was " actual)))))

(defn assert-false [expr] (assert (not expr)))    

;;conj
(assert-equals (conj [1 2] 3) [1 2 3])

;;compose
(defn compose-fn [nums]
  (map (comp #(+ 1 %) 
             #(* 2 %) )
       (filter even? nums))
  )
(assert-equals (compose-fn [1 2 3 4]) [5 9])

;;maps
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
;;sets
(assert-equals #{:a :b :c} #{:b :c :a} )


; elements functions of sets and vice-versa
(def myset #{:a :b :c})
(assert-equals (:a myset) :a)

;;vectors
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

;;filter
(defn filter-minors [persons] (filter #(> 18 (:age %)) persons))

(assert-equals (filter-minors [john jack]) [john])


