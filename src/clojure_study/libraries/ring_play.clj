(ns clojure-study.libraries.ring-play
  (:use [clojure-study.assertion])
  (:require [ring.adapter.jetty :as r])
  )

(println "start")

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello World1"})

(defn logger [handler]
  (fn [request]
    (println "Request is" request)
    (handler request)))

(def app (-> handler
           logger))

(defonce server (r/run-jetty #'app {:port 8080 :join? false}))
(.stop server)
(.start server)

