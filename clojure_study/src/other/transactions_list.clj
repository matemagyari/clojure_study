(ns clojure.transactions-list.clj
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(defn take-csv
  "Takes file name and reads data."
  [fname]
  (with-open [file (io/reader fname)]
    (csv/read-csv (slurp file) :separator \;)))

(defn convert-amount
  "Converts the string representation of a number to a Double"
  [s]
  (-> s
      (.replaceAll "\\." "")
      (.replaceAll "\\," ".")
      read-string))

(defn line->transaction
  "Transform a line from the CSV file into a transaction"
  [line]
  (let [get (fn [n] (nth line n))]
    {:amount (convert-amount (get 5)) :date (get 1) :text (get 2)}))

(defn read-transactions
  "Takes file and converts its content to a list of structured transactions"
  [fname]
  (->> fname
       take-csv
       (drop 1)                                             ;;drop column names
       (map line->transaction)))

;(def file "/Users/david/Downloads/IKS-00000000650573511493104569540.csv")
(def file "/Users/david/Downloads/IKS-00000000650573511500916793097.csv")
;(def transactions
;  (->> (read-transactions file)
;      (filter #(.contains (:text %) "IMG"))))

(->> (read-transactions file)
     (sort-by :amount)
     (filter #(neg? (:amount %)))
     ;(filter #(or
     ;           (str/includes? % "STROECK")
     ;           (str/includes? % "MERKUR")
     ;           (str/includes? % "BILLA")
     ;           (str/includes? % "SPAR")
     ;           (str/includes? % "HOFER")))
     ;(map :amount)
     ;(reduce +)
     )
