(ns FirstClojure.core)

;;=========================
(defn assert-equals [actual expected]
  (when-not (= actual expected)
    (throw 
      (AssertionError. 
        (str "Expected " expected " but was " actual)))))

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

(assert-equals (assoc jack :gender "M" :title "Mr")
               {:name "Jack" :age 31 :gender "M" :title "Mr"}) 

;;sets
(assert-equals #{:a :b :c} #{:b :c :a} )


; elemets functions of sets and vice-versa
(def myset #{:a :b :c})
(assert-equals (:a myset) :a)

;;vectors
(assert-equals (assoc [1 2] 0 333)
               [333 2]) 
(assert-equals (assoc [1 2] 2 333)
               [1 2 333]) 
(assert-equals (nth [1 2] 1)
               2) 
(assert-equals (get [1 2] 1)
               2) 
(assert-equals (get [1 2] 5 6)
               6) 

;;filter
(defn filter-minors [persons] (filter #(> 18 (:age %)) persons))

(assert-equals (filter-minors [john jack]) [john])


