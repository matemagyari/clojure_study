(ns
  ^{:author mate.magyari}
  other.deczip
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))


(defn deczip [a b]
  (println [a b])
  (let [str->seq (fn [n] (-> n str vec))
        calc (fn [as bs acc]
               (cond
                 (empty? as) (concat acc bs)q
                 (empty? bs) (concat acc as)
                 :else (recur
                         (rest as)
                         (rest bs)
                         (conj acc (first as) (first bs)))))
        result-coll (calc (str->seq a) (str->seq b) [])
        result-str (apply str result-coll)]
    (Integer/parseInt result-str)))

(def length-prop
  (let [len (fn [n] (-> n str count))
        lenght-match? (fn [a b]
                        (= (+ (len a) (len b))
                          (len (deczip a b))))]
    (prop/for-all [a gen/s-pos-int b gen/s-pos-int] (lenght-match? a b))))

(println
  (tc/quick-check 100 length-prop))