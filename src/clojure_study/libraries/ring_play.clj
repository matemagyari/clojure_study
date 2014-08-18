(ns clojure-study.libraries.ring-play
  (:use [clojure-study.assertion])
  (:require [ring.adapter.jetty :as r])
  )

(println "start")

(defn app [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello World"})

;(defonce server (r/run-jetty #'app {:port 8080 :join? false}))
;(.stop server)

