(ns ufo-data.uforunner
  (:require [ufo-data.model :as model]))

(defn -main [& args]
  (println
    (count
      (model/read-data "/Users/mate.magyari/Downloads/ufo_awesome.tsv"))))
