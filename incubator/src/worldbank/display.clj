(ns worldbank.display)

(defn- value->bar [value]
  (apply str (repeat value "*")))

(defn- value->hist-bar [{:keys [date value] :as tv} normalizer]
  (if value
    (let [bar (-> value read-string (/ normalizer) value->bar)]
      (str date " " bar " " value))
    (str date " No data available")))

(defn print-histogram [time-value-pairs]
  (let [vals-tr (comp
                  (map :value) (filter some?) (map read-string)) ; transducer
        max-val (apply max (sequence vals-tr time-value-pairs))
        normalizer (/ max-val 30)]
    (doseq [tv time-value-pairs]
      (println (value->hist-bar tv normalizer)))))
