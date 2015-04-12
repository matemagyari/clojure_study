(ns
  ^{:author mate.magyari}
  clojure-study.ideas.dependencies
  (:require [clojure.repl :as repl]
            [clojure.walk :as walk]
            [clojure.string :as string]
            [clojure.tools.trace :as trace]
            [clojure.core.async :as async]
            [clojure-study.mini-projects.swarm.app :as my]))



(defn get-ns-name [fn-symbol]
  (some->> fn-symbol resolve meta :ns ns-name))

(defn full-fn-symbol [fn-symbol]
  (if (-> fn-symbol name (.contains "/"))
    fn-symbol
    (let [ns-name (some->> fn-symbol resolve meta :ns ns-name name)]
      (symbol (str ns-name "/" (name fn-symbol))))))

(defn ns-plus-fn-symbol [ns-s fn-s]
  (symbol
    (str (name ns-s) "/" (name fn-s))))

(defn find-fn-symbols [current-ns relevant? element]
  ; (println "hoola" element)
  (let [var-and-relevant? (fn [x] (and
                                    (var? (resolve x))
                                    (relevant? x)))]
    (cond
      (string? element) nil
      (sequential? element) (->> element flatten distinct (filter some?))
      (not (symbol? element)) nil
      (var-and-relevant? element) element
      (var-and-relevant? (ns-plus-fn-symbol current-ns element)) (ns-plus-fn-symbol current-ns element)
      :else nil)))

(defn find-referred-fns [fn-symbol ns-exclusions]
  {:pre [(vector? ns-exclusions) (symbol? fn-symbol)]
   :post [(sequential? %)]}
  (let [relevant? (fn [x] (not (.contains ns-exclusions (get-ns-name x))))
        fn-ns (get-ns-name fn-symbol)
        walk-fn (partial find-fn-symbols fn-ns relevant?)
        result (some->> fn-symbol
                 clojure.repl/source-fn
                 read-string
                 (clojure.walk/postwalk walk-fn))]
    (if result result []))) ; nil -> []

(defn build-graph [root-fn-symbol ns-exclusions]
  {:pre [(vector? ns-exclusions) (symbol? root-fn-symbol)]
   :post [(sequential? %)]}
  (let [children (find-referred-fns root-fn-symbol ns-exclusions)
        children (filter #(not= root-fn-symbol %) children)
        node {:fn root-fn-symbol
              :children children}
        ;_ (println (str "\nNode: " node "\n"))
        children-nodes (flatten (for [c children]
                         (build-graph c ns-exclusions)))]
    (cons node children-nodes)))

(assert (= 'clojure.core (get-ns-name 'inc)))
(assert (= 'clojure.core (get-ns-name 'clojure.core/inc)))
(assert (= 'clojure.core/inc (full-fn-symbol 'clojure.core/inc)))
(assert (= 'clojure.core/inc (full-fn-symbol 'inc)))

(assert (= 'aaa/bbb (ns-plus-fn-symbol 'aaa 'bbb)))

(assert (= ['defn 'clojure.walk/postwalk 'clojure.walk/walk 'partial] (find-referred-fns 'clojure.walk/postwalk [])))
(assert (= ['clojure.walk/postwalk 'clojure.walk/walk] (find-referred-fns 'clojure.walk/postwalk ['clojure.core])))

;(trace/trace-ns clojure-study.ideas.dependencies)
;(println (build-graph 'clojure.walk/postwalk ['clojure.core]))
; 'clojure.core.async/alt!!
(def result (build-graph 'clojure-study.mini-projects.swarm.app/run-show! ['clojure.core]))
(println result)
(println (doseq [x result]
           (println x)))

;; (meta (resolve 'inc))



