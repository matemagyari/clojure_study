(ns
  ^{:author mate.magyari}
  clojure-study.ideas.sampler)

(defn sampler [f freq]
  (let [counter (atom 0)]
    (fn [& args]
      (swap! counter inc)
      (when (= freq @counter)
        (reset! counter 0)
        (apply f args)))))

(def x (sampler println 3))
