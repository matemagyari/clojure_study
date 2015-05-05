(ns clojure-study.transducers)

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


;; reducing function signature: whatever, input -> whatever

;; A transducer (sometimes referred to as xform or xf) is a transformation from one reducing function to another:
;; transducer signature: (whatever, input -> whatever) -> (whatever, input -> whatever)
(defn identity-tr
  "f is a reducer function. This function returns the input function."
  [f]
  (fn [whatever input]
    (f whatever input)))

(let [my+ (identity-tr +)]
  (assert= 10 (reduce my+ [2 3 5])))

(defn doubler-tr
  "f is a reducer function. This function returns a function that doubles every input before passing it to f."
  [f]
  (fn [whatever input]
    (f whatever (* 2 input))))

(let [my+ (doubler-tr +)]
  (assert= 20 (reduce my+ 0 [2 3 5])))


(defn nilsafe-tr
  "f is a reducer function. This function returns a function that passes input only to f if it's not nil"
  [f]
  (fn [whatever input]
    (if input (f whatever input) whatever)))

(let [my+ (nilsafe-tr +)]
  (assert= 10 (reduce my+ [2 nil 3 5])))

;this transducer composes the previous 2.
(def nilsafe-doubler-tr (comp nilsafe-tr doubler-tr))

(let [my+ (nilsafe-doubler-tr +)]
  (assert= 20 (reduce my+ 0 [2 nil 3 5])))


