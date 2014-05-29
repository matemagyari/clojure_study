(ns clojure-study.lazy-seqs
  (:use clojure-study.assertion))

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

