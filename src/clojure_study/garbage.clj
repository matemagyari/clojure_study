(ns clojure-study.garbage
  (:require [clojure.core.match :as m]))



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


