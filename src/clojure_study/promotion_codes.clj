(ns clojure-study.promotion-codes
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))


(defn prom-code-generator [prefix medium postfix-len postfix-charset]
  (let [lazy-rnd-seq (repeatedly #(rand-nth postfix-charset))
        lazy-code (take postfix-len lazy-rnd-seq)
        postfix (apply str lazy-code)]
    (str prefix medium postfix)))

(defn prom-codes-generator [prefix medium postfix-len postfix-charset num]
  {:post [(= (count %) num) (apply distinct? %)]}
  (take num (repeatedly #(prom-code-generator prefix medium postfix-len postfix-charset))))

(defn to-csv [a-seq]
  (with-open [out-file (io/writer "codes.csv")]
    (csv/write-csv out-file a-seq)))

(defn generate-codes-to-csv [prefix medium postfix-len postfix-charset num]
  (let [codes (prom-codes-generator prefix medium postfix-len postfix-charset num)]
    (to-csv [codes])))

(def alphanumeric-chars (concat
                           (map char (range 65 91)) ;[A-Z]
                           (range 10))) ;[0-9]

(generate-codes-to-csv "VIRGIN" "C" 5 alphanumeric-chars 1000)

;(prom-codes-generator "VIRGIN" "C" 4 alphanumeric-chars 6)

