#!/usr/bin/env lein-exec

(ns other.echo-tcp-server2
  (:require [clojure.java.io :as io])
  (:import (java.net Socket
                     ServerSocket)))

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

(defn handle-connection!
  "Handles a connection to the TCP server. It assumes the incoming connections are
  either from the Load Balancer's health check, and ignores them, or from a client
  sending timestamps. It compares the received timestamps with the actual one and
  calculates the difference"
  [server-socket continue]

  (with-open [client-socket (.accept server-socket)
              reader (io/reader client-socket)]

    (let [last-msg (atom "starting value")
          msgs (atom 0)
          diffs (atom 0)
          continue? (fn [msg] (and (not= msg "end")
                                   (not= msg "stop")
                                   (some? msg)))]

      (while (continue? @last-msg)

        (let [msg-in (receive! reader)]

          ;stop the server if received 'stop' message
          (when (= "stop" msg-in)
            (reset! continue false))

          (reset! last-msg msg-in)
          (when (and (some? msg-in) (continue? msg-in))
            (let [diff (- (System/currentTimeMillis)
                          (read-string msg-in))]
              (println (str "Diff is: " diff))
              (swap! msgs inc)
              (swap! diffs + diff)))))

      ;healthcheck calls should pass this
      (when (pos? @msgs)
        (println (str "Average diff is: " (double (/ @diffs @msgs))))
        (println (str "Connection " client-socket " closed"))))))


(defn start-server!
  "Starts up a TCP echo server which serves one client"
  [port]
  (println "Server starts")

  (with-open [server-socket (ServerSocket. port)]
    (let [continue (atom true)]
      (while @continue
        (handle-connection! server-socket continue))
      (println "Server stops"))))


(defn start-client!
  "Sends a given number TCP packets to the remote host.
  The payloads are the current time in millis"
  [host port msgs-num]

  (println (str "Client starts to send " msgs-num " packages to [" host ":" port "]"))

  (with-open [socket (Socket. host port)
              writer (io/writer socket)]
    (dotimes [n msgs-num]
      (println (str "Sending " n ". message"))
      (send-msg! writer (System/currentTimeMillis))
      (Thread/sleep 100))
    (send-msg! writer "stop")
    (send-msg! writer "end"))

  (println "Client stopped"))

(let [args *command-line-args*
      arg (second args)]
  (condp = arg
    "server" (let [port (read-string (nth args 2))]
               (start-server! port))
    "client" (let [[host port msgs]
                   host (nth args 2)
                   port (read-string (nth args 3))
                   msgs (read-string (nth args 4))]
               (start-client! host port msgs))
    (println (str "Unexpected value [" arg "]"))))

