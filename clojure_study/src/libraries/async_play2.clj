(ns libraries.async-play2
  (:require [clojure.core.async :refer [go go-loop chan close! <! <!! >! >!!
                                        timeout filter< put! take! pub sub unsub unsub-all
                                        thread alts! alts!!]
             :as a]
            [clj-http.client :as client]
            [clojure.test :as test]))



(def c (chan 100))
(def go-chan
  (a/go-loop []
    (println "Loops starts")
    (if-let [in-msg (a/<! c)]
      (do
        (println (str "Incoming: " in-msg))
        (recur))
      (println "Channel closed"))))

(defn aaa []
  (let [get-data (fn [url]
                   (client/get url {:as :json}))
        c1 (a/go (get-data (str "http://api.worldbank.org/en/indicator?format=json&per_page=" 1)))
        c2 (a/go (get-data (str "http://api.worldbank.org/en/indicator?format=json&per_page=" 1)))]
    (a/go
      (println "Let's do it")
      (let [[v c] (a/alts! [c1 c2])]
        (println "Result" v c)))))

(defn -main []
  (do
    (println "start")
    (>!! c "First")
    (>!! c "Second")
    ))

(-main)