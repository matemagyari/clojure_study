(ns
  ^{:author mate.magyari}
  clojure_study.transducers)

(defn assert= [& args]
  (assert (apply = args)))

;;a reducing function
(defn mean-reducer [reduction x]
  (-> reduction
    (update-in [:count] inc)
    (update-in [:sum] + x)))

;; it can be used in reduce
(assert= {:sum 10 :count 4}
  (reduce mean-reducer {:sum 0 :count 0} [1 2 3 4]))

;; a transducer transforms a reducing function to another reducing function
;; transform mean-counter reducing function to another reducing function. The transducer is (map inc)
(let [a-transducer (map inc)
      new-reducing-fn (a-transducer mean-reducer)]
  (assert= {:sum 14 :count 4}
    (reduce new-reducing-fn {:sum 0 :count 0} [1 2 3 4])
    ;; reduce with a transformation (no laziness)
    (transduce a-transducer mean-reducer {:sum 0 :count 0} [1 2 3 4])))

;==========================
(let [xform (comp
              (filter even?)
              (map inc))
      from [1 2 3 4]]
  (assert=
    [3 5]
    ;; reduce with a transformation (no laziness)
    (transduce xform conj [] from)
    ;;build one collection from a transformation of another, again no laziness
    (into [] xform from)
    ;; lazily transform the data
    (sequence xform from)))

