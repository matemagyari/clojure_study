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


(assert= {:sum 10 :count 4}
  (reduce mean-reducer {:sum 0 :count 0} [1 2 3 4]))

;; transform mean-counter to another reducing function. The transducer is (map inc)
(let [a-transducer (map inc)
      new-reducing-fn (a-transducer mean-reducer)]
  (assert= {:sum 14 :count 4}
    (reduce new-reducing-fn {:sum 0 :count 0} [1 2 3 4])
    (transduce a-transducer mean-reducer {:sum 0 :count 0} [1 2 3 4])))

;==========================
(let [increment-even (comp
                       (filter even?)
                       (map inc))]
  (assert=
    [3 5]
    (into [] increment-even [1 2 3 4])
    (sequence increment-even [1 2 3 4])))

