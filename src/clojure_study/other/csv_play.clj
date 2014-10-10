(ns
  ^{:author mate.magyari}
  clojure_study.other.csv-play
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))


(defn csv->seq [file]
  (with-open [out-file (io/reader "/Users/mate.magyari/Downloads/result_2.csv")]
    (csv/read-csv (slurp file))))

(defn to-csv [a-seq]
  (with-open [out-file (io/writer "codes.csv")]
    (csv/write-csv out-file a-seq)))

(count
  (let [[result] (csv->seq "/Users/mate.magyari/Downloads/result_2.csv")]
    result))


