(ns ufo-data.model
  (:require [clojure.java.io :as io]
            [clojure.core.reducers :as r]
            [clojure.string :as str]
            ;[clojure.data.json :as json]
            [clojure.data.csv :as csv]
            [clj-time.format :as tf]
            ;[ufo-data.text :as t]
            ;[ufo-data.util :refer :all]
            ;[me.raynes.fs :as fs]
            )
  (:import [java.lang StringBuffer]))

(defrecord UfoSighting
  [sighted-at reported-at location shape duration description year month season])

(defn ->ufo [row]
  (let [row (cond
              (> (count row) 6)
              (concat (take 5 row)
                [(str/join \t (drop 5 row))])
              (< (count row) 6)
              (concat row (repeat (- 6 (count row)) nil))
              :else row)]
    (apply ->UfoSighting (concat row [nil nil nil]))))

(def date-formatter (tf/formatter "yyyyMMdd"))
(defn read-date [date-str]
  (try
    (tf/parse date-formatter date-str)
    (catch Exception ex
      nil)))

(defn coerce-fields-old [ufo]
  (assoc ufo
    :sighted-at (read-date (:sighted-at ufo))
    :reported-at (read-date (:reported-at ufo))))


(defn coerce-fields [ufo]
  (-> ufo
    (update-in [:sighted-at] read-date)
    (update-in [:reported-at] read-date)))

(defn read-data-old
  [filename]
  (with-open [f (io/reader filename)]
    (->> (csv/read-csv f :separator \tab)
      vec
      (r/map ->ufo)
      (r/map coerce-fields)
      (into []))))

(defn read-data
  [filename]
  (with-open [f (io/reader filename)]
    (let [raw-data (-> f (csv/read-csv :separator \tab) vec)
          trans-fn (map
                     (comp coerce-fields ->ufo))]
      (into [] trans-fn raw-data))))

(def data (read-data "/Users/mate.magyari/Downloads/ufo_awesome.tsv"))

(time (let [data (read-data "/Users/mate.magyari/Downloads/ufo_awesome.tsv")] nil))
(time (let [data (read-data-old "/Users/mate.magyari/Downloads/ufo_awesome.tsv")] nil))

(defn shape-trans []
  (comp
    (map :shape)
    (filter (comp not str/blank?))
    (map str/trim)))

(def r1 (sequence (shape-trans) data))

(def r2 (->> (frequencies r1)
          (sort-by second)
          (reverse)
          (take 10)))

