(ns
  patterns.ideas.tree
  (:require [clojure.core.match :as m]
            [clojure.core.typed :as t]
            [clojure-study.assertion :as a]))

(t/defalias Node
  (t/Rec [a]
    (t/HMap :mandatory {:value a :left (t/U (Node a) :empty-tree) :right (t/U (Node a) :empty-tree)}
            :complete? true)))

(defn node [val left right]
  {:value val :left left :right right})

(defn singleton [x]
  (node x :empty-tree :empty-tree))

(defn tree-insert [x t ord]
  (m/match t
    :empty-tree (singleton x)
    {:value val
     :left left
     :right right} (condp = (ord x val)
                     :eq t
                     :gt (node val left (tree-insert x right ord))
                     :lt (node val (tree-insert x left ord) right))))

(defn tree-elem? [x t ord]
  (m/match t
    :empty-tree false
    {:value val
     :left left
     :right right} (condp = (ord x val)
                         :eq true
                         :gt (tree-elem? x right ord)
                         :lt (tree-elem? x left ord))))

;;an ord function for numbers
(defn num-ord [x y]
  (cond
    (= x y) :eq
    (> x y) :gt
    (< x y) :lt))

(defn build-tree [elements ord]
  (reduce #(tree-insert %2 %1 ord) :empty-tree elements))

(defn print-tree
  ([t] (print-tree t 0))
  ([t depth]
    (let [tab (apply str (repeat depth "\t"))]
      (if (= :empty-tree t)
        (println "Empty")
        (println (str "Value: " (:value t) "\n"
                   tab "Left: " (print-tree (:left t) (inc depth)) "\n"
                   tab "Right: " (print-tree (:right t) (inc depth))))))))

;; [2 5 7 3 5 9 6 7 3 5 1 4]
(def a-tree (build-tree [8 6 4 1 7 3 5] num-ord))
(assert (tree-elem? 7 a-tree num-ord))
(assert (not (tree-elem? 9 a-tree num-ord)))
(print-tree a-tree)
(println a-tree)
