(ns clojure_study.other.net)


(defn copy-uri-to-file [uri file]
  (with-open [in (clojure.java.io/input-stream uri)
              out (clojure.java.io/output-stream file)]
    (clojure.java.io/copy in out)))

(copy-uri-to-file
  "https://raw.githubusercontent.com/shidh/ML_Hackers_Data/master/01-Introduction/data/ufo/ufo_awesome.tsv"
  "/Users/mate.magyari/Downloads/ufo_awesome.tsv")