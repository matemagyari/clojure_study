(ns clojure-study.clojure.lazy-seqs
  (:require [clojure-study.clojure.assertion :as a]))

(defn int-stream
  ([] (int-stream 1))
  ([x] (cons x (lazy-seq (int-stream (inc x))))))

(comment
  (def char-set [1 2 3])

  (defn perms [a-seq]
    {:pre [(sequential? a-seq)]}
    ;(println "seq" a-seq)
    (if (empty? a-seq)
      []
      (for [elem a-seq
            perm-rem (perms (remove #(= % elem) a-seq))]
        (conj perm-rem elem)
        )))

  (defn perms2 [a-seq]
    {:pre [(sequential? a-seq)]}
    (dorun (println "seq" a-seq))
    (if (empty? a-seq)
      []
      (for [elem a-seq
            :let [the-rest (filter #(not= % elem) a-seq)]
            perm-rem (perms2 the-rest)]
        (conj perm-rem elem)
        )))


  (def r (perms2 char-set))
  (println "result" r)
  )

(let [action-logger (atom [])
      logging-fn (fn [f a]
                   (fn [& x]
                     (swap! action-logger conj a)
                     (apply f x)))
      logging-inc (logging-fn inc :m)
      logging-even? (logging-fn even? :f)]
  (a/assert= [2 3 4] (map logging-inc [1 2 3]))
  (a/assert= @action-logger [:m :m :m])
  (reset! action-logger [])
  (a/assert= [2 4] (filter logging-even? [1 2 3 4]))
  (a/assert= @action-logger [:f :f :f :f])
  ;filter then map on non-lazy sequence
  (reset! action-logger [])
  (a/assert= [3 5 7] (->> [1 2 3 4 5 6 7]
                             (filter logging-even?)
                             (map logging-inc)))
  (a/assert= @action-logger [:f :f :f :f :f :f :f :m :m :m])
  ;map then filter on non-lazy sequence
  (reset! action-logger [])
  (a/assert= [2 4 6 8] (->> [1 2 3 4 5 6 7]
                               (map logging-inc)
                               (filter logging-even?)))
  (a/assert= @action-logger [:m :m :m :m :m :m :m :f :f :f :f :f :f :f])
  ;map then filter on lazy sequence
  (reset! action-logger [])
  (a/assert= [2 4 6 8] (->> (range)
                               (map logging-inc)
                               (filter logging-even?)
                               (take 4)))
  ;(a/assert-equals @action-logger [:m :m :m :m :m :m :m :f :f :f :f :f :f :f])
  ;filter then map on lazy sequence
  (reset! action-logger [])
  (assert (some? (->> (range)
                   (filter logging-even?)
                   (map logging-inc)
                   (take 64))))
  ;(a/assert-equals @action-logger [:m :m :m :m :m :m :m :f :f :f :f :f :f :f])
  )
;(letfn [(twos [] (cons 2 (lazy-seq )))])

