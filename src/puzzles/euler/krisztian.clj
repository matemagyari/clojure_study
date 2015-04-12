(ns
  ^{:author mate.magyari}
  puzzles.euler.krisztian)

(defn steps
  ([nums] (if (empty? nums) nil (steps nums [])))
  ([nums acc]
    (let [jumprange (first nums)
          new-acc (conj acc jumprange)]
      (cond
        (>= jumprange (count nums)) new-acc
        :else (let [rest (for [i (range 1 (inc jumprange))
                               :let [c (steps (drop i nums) new-acc)]
                               :when (-> c empty? not)] c)]
                (cond
                  (empty? rest) nil
                  :else (apply min-key count rest)))))))


(defn steps2 [nums]
  (if (-> nums empty? not)
    (let [jumprange (first nums)]
      (cond
        (>= jumprange (count nums)) [jumprange]
        :else (let [rest (for [i (range 1 (inc jumprange))
                               :let [c (steps2 (drop i nums))]
                               :when (-> c empty? not)]
                           (cons jumprange c))]
                (if (-> rest empty? not)
                  (apply min-key count rest)))))))

(println (steps2 [0,1]))
(println (steps2 [1,0,1]))
(println (steps2 [1,1,1]))
(println (steps2 [3,3,0,5,4]))

