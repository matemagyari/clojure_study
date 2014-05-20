(ns clojure-study.macros2)

(def c1 [1 2])
(def c2 ["a" "b"])

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

(defmacro ae [a e]
  `(= ~a ~e))

(defmacro dbg2 [expr] 
  `(let [expr# ~expr] 
     (println "evaluating" '~expr)
     expr#))

(defn pp [ [& args] fun]
  (apply fun args))

;;practice
(defmacro dbg [& args]
  `(let [fun# (first (vector ~@args)) 
         args# (rest (vector ~@args))
         res# (apply fun# args#)] 
     (println '~args "=" res# ) 
     res# ))

(defmacro forloop [ [sym start end] & body]
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