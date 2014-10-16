(ns
  ^{:author mate.magyari}
  clojure_study.other.sampler)

(defn sampler [f freq]
  (let [counter (atom 0)]
    (fn [& args]
      (swap! counter inc)
      (when (= freq @counter)
        (reset! counter 0)
        (apply f args)))))

(def x (sampler println 3))
