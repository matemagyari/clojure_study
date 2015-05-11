(ns other.garbage
  (:require [clojure.core.match :as m])
  )



(defn compact-combinations [number]
  (let [num->chars { \2  "ABC" \3  "DEF" \4  "GHI" \5  "JKL" 
                    \6  "MNO" \7  "PQRS" \8  "TUV" \9  "WXYZ"}
        red-fn (fn [acc d]
                 (m/match (num->chars d)
                   nil acc
                   chs (for [c chs
                             i acc]
                           (str i c))))] 
    (reduce red-fn [""] number)))

(let [combinations (fn [number] 
                     (let [num->chars {\2  "ABC" \3  "DEF" \4  "GHI" \5  "JKL" 
                                       \6  "MNO" \7  "PQRS" \8  "TUV" \9  "WXYZ"}
                           red-fn (fn [acc d]
                                    (m/match (num->chars d)
                                      nil acc
                                      chs (for [c chs
                                                i acc]
                                              (str i c))))] 
                       (reduce red-fn [""] number)))]
  (println (combinations "514")))

(def x 5)

(defn list2map [a-seq]
  (let [len (count a-seq)
        a-cycle (cycle a-seq)
        shifted (drop 1 a-cycle)
        the-keys (->> a-cycle (take-nth 2) (take len))
        the-vals (->> shifted (take-nth 2) (take len))]
    (zipmap the-keys the-vals)))

(list2map [1 2 3 4 5 6])






