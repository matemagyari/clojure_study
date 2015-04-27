(ns
  ^{:author mate.magyari}
  mini-projects.twitter-reader.garbage
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

(def conf (-> "/Users/mate.magyari/twitter.conf" slurp edn/read-string))

(println (class (:consumer-key conf)))
