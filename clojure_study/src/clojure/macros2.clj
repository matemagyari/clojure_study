(ns clojure.macros2)

(def c1 [1 2])
(def c2 ["a" "b"])


(defmacro visp [v]
  (cons (first v) (rest v)))

(assert (= 3 (visp (+ 1 2))))
(assert (= 3 (visp [+ 1 2])))

(defmacro squares [v]
  (list 'map '#(* % %) v))

(assert (= [4 9] (squares [2 3])))

(defmacro squares2 [v]
  `(map #(* % %) ~v))

(assert (= [4 9] (squares2 [2 3])))

;(defmacro comb [ fun & colls ]
; `(let [colls# ~colls
;         symbs# (->> (gensym) repeat (take (count colls#)))
;seq-exprs# (for [c# colls#] c#)
;seq-exprs# (vec (interleave symbs# seq-exprs#))
;         ]
;(println seq-exprs#)
;(println "symbs#" symbs#)
;    12
;(for seq-exprs# '('str symbs#))
;  ))

;(comb "a" c1 c2 )



(defmacro assert-equals [actual expected]
  `(let [actual-value# ~actual]
     (when-not (= ~actual ~expected)
       (throw
         (AssertionError.
           (str "Expected '" '~actual "' to be " ~expected
             " but was " actual-value#))))))

(defmacro twice [e] `(do ~e ~e))
(defmacro n-times [e n]
  (loop [acc []
         i n]
    (if (zero? i) `(do ~@acc)
      (recur (cons e acc) (dec i)))))

(twice (println "foo"))
(n-times (println "foo") 3)

;; =============== QUOTING =================
(assert-equals `(+ 1 2) `(+ 1 ~(inc 1)))
(assert-equals (list '+ 1 2) '(+ 1 2))


;;================= unless =============
(defmacro unless [test val-true val-false]
  `(if ~test ~val-false ~val-true))

(defmacro unless-2 [test val-true val-false]
  (list 'if test val-false val-true))

(assert-equals "T" (unless (> 0 1) "T" "F"))
(assert-equals "T" (unless-2 (> 0 1) "T" "F"))
(assert-equals "F" (if (> 0 1) "T" "F"))

;;================= reverse-it =============
(defmacro reverse-it [expr]
  (let [f (-> expr first name clojure.string/reverse symbol)]
    (cons f (rest expr))))

(assert-equals 5 (reverse-it (cni 4)))

;;=================== INFIX ==============
(defmacro infix [expression]
  (let [op (second expression)
        arg-2 (last expression)
        arg-1 (first expression)]
    (list op arg-1 arg-2)))

(macroexpand-1 '(infix
                  (2 + 3)))

(assert-equals 5 (infix
                   (2 + 3)))

;;=================== REVERSE ==============
(defmacro rev-1 [code]
  (reverse code))

(assert-equals (+ 1 2)
  (rev-1 (2 1 +)))
;;=================== WHEN ==============
(defmacro when-1 [test & body]
  `(if ~test (do ~@body)))

(defmacro when-2 [test & body]
  (list 'if test (cons 'do body)))

(assert-equals 5 (when-1 true (println "w1 test1") 5))
(assert-equals nil (when-1 false (println "w1 test2") 5))
(assert-equals 5 (when-2 true (println "w2 test1") 5))
(assert-equals nil (when-2 false (println "w2 test2") 5))

;;===================DEBUG==================
(defmacro deb [expr]
  )

;;=================== FOREACH ==============
(defmacro foreach [from to body]
  `(loop [i# ~from]
     ~body
     (if (> i# ~to) nil
       (recur (inc i#)))))

(comment
  (foreach 0 3 (println "a"))
  (defmacro foreach2 [index from to body]
    `(loop [i# ~from]
       ~body
       (if (> i# ~to) nil
         (recur (inc i#)))))
  )

(foreach 0 3 (println "a"))


(defmacro ae [a e]
  `(= ~a ~e))

(defmacro dbg2 [expr]
  `(let [expr# ~expr]
     (println "evaluating" '~expr)
     expr#))

(defn pp [[& args] fun]
  (apply fun args))

;;practice
(defmacro dbg [& args]
  `(let [fun# (first (vector ~@args))
         args# (rest (vector ~@args))
         res# (apply fun# args#)]
     (println '~args "=" res#)
     res#))

(defmacro forloop [[sym start end] & body]
  `(let [start# ~start end# ~end]
     (loop [~sym start#]
       (when (<= ~sym end#)
         ~@body
         (recur (inc ~sym))))))

(defmacro forall [fun & colls]
  `(let [fun# ~fun
         colls# (vector ~@colls)
         coll-count# (count colls#)
         syms# (take coll-count# (repeat (gensym)))
         s-expr# (interleave syms# colls#)
         v-expr# (vector s-expr#)]
     (println "s-expr#" s-expr#)
     (println "v-expr#" v-expr#)
     (println "vec" (vector? s-expr#))
     (println "vec2" (vector? v-expr#))
     (for v-expr# (println "hi"))
     ))

;(macroexpand-1 '(forloop [i 1 3] (println i)))


