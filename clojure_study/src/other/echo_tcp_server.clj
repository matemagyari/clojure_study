#!/usr/bin/env lein-exec

(ns other.echo-tcp-server
  (:require [clojure.java.io :as io])
  (:import (java.net Socket
                     ServerSocket)))

;(require '[clojure.java.io :as io])
;(import (java.net ServerSocket))

(defn receive!
  "Read a line of text"
  [reader]
  (.readLine reader))

(defn send-msg!
  "Send textual message closed by a line break"
  [writer msg]
  (do
    (.write writer (str msg "\n"))
    (.flush writer)))

(defn start-server!
  "Starts up a TCP echo server which serves one client"
  [port]
  (let [last-msg (atom nil)]

    (println "Server starts")

    (with-open [server-socket (ServerSocket. port)
                client-socket (.accept server-socket)
                writer (io/writer client-socket)
                reader (io/reader client-socket)]

      (while (not (= @last-msg "end"))
        (let [msg-in (receive! reader)]
          (reset! last-msg msg-in)
          (println (str "Message is [" msg-in "]"))
          (send-msg! writer (System/currentTimeMillis))))))

  (println "Server stops"))

(defn start-client!
  "Sends TCP requests"
  [host port msgs-num]

  (println "Client starts")

  (let [avg-turn-around (atom 0)
        avg-out-time (atom 0)
        avg-in-time (atom 0)]

    (with-open [socket (Socket. host port)
                writer (io/writer socket)
                reader (io/reader socket)]

      (dotimes [_ msgs-num]
        (let [start-ts (System/currentTimeMillis)
              _ (send-msg! writer start-ts)
              received-msg (read-string (receive! reader))
              end-ts (System/currentTimeMillis)
              turn-around (- end-ts start-ts)
              out-time (- received-msg start-ts)
              in-time (- end-ts received-msg)]

          (swap! avg-turn-around + turn-around)
          (swap! avg-out-time + out-time)
          (swap! avg-in-time + in-time)

          (println (str "turn-around: " turn-around " out-time: " out-time " in-time:" in-time))))

      (send-msg! writer "end"))

    (println (str "Average turn-around: " (/ @avg-turn-around msgs-num)  "\n"
                  "Average out time: " (/ @avg-out-time msgs-num)  "\n"
                  "Average in time: " (/ @avg-in-time msgs-num)  "\n")))

  (println "Client stopped"))

(let [args *command-line-args*]
  (let [arg (second args)]
    (condp = arg
      "server" (start-server! 8888)
      "client" (start-client! "localhost" 8888 10)
      (println (str "Unexpected value [" arg "]")))))

