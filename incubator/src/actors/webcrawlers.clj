(ns actors.webcrawlers
  ;;(:import java.net.UnknownHostException java.io.IOException java.io.FileNotFoundException)
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.core.async :as async]))


(defrecord CrawlerMessage [url occurrence])
(defrecord Message [type body])

(def html-link-regex-pattern (re-pattern #"\b(([\w-]+://?|www[.])[^\s()<>]+(?:\([\w\d]+\)|([^[:punct:]\s]|/)))"))

(defn send-update! [occurrences]
  (println occurrences))

(defn center-actor
  "Updates the occurrences"
  [{:keys [state msg]}]
  (let [{url :url occurrence :occurrence} msg
        sorted-occurences (sort-by val >
                            (assoc-in (:occurences state) url occurrence))
        new-state (assoc-in state [:occurrences url] occurrence)
        msg (->Message :report (:occurences new-state))]
    {:state new-state :msgs [msg]}))

(defn crawler-actor [{:keys [state msg]}]
  (let [x 1] 1))


(defn occurrences-in-text [text search-word]
  (re-seq (re-pattern search-word) text))

(defn num-of-occurrences-in-text [text search-word]
  (count (re-seq (re-pattern search-word) text)))

(defn occurrences-on-url!-old [url search-word]
  (with-open [rdr (io/reader url)]
    (->> (line-seq rdr)
      (map #(num-of-occurrences-in-text % search-word))
      (reduce +))))

(defn occurrences-on-url! [content search-word]
  (try
    (num-of-occurrences-in-text (slurp url) search-word)
    (catch Exception e 0)))

(defn get-links [content]
  (let [results (re-seq html-link-regex-pattern content)]
    (map first results)))

(defn print-html! [url]
  (with-open [rdr (io/reader url)]
    (doseq [line (line-seq rdr)]
      (println line "--end"))))

(defn crawl [{:keys [url search-word report-channel depth]}]
  (async/go
    (println (str depth " crawl: " url))
    (let [content (try
                    (slurp url)
                    (catch Exception e ""))
          occurrences (num-of-occurrences-in-text content search-word)]
      (async/>! report-channel {:url url
                                :occurrences occurrences})
      (when (pos? depth)
        (doseq [link (get-links content)]
          (crawl {:url link
                  :search-word search-word
                  :report-channel report-channel
                  :depth (dec depth)}))))))



(def rep-ch (async/chan 10))

;; Print out the messages sent by the actor
(async/go-loop []
  (when-let [msg (async/<! rep-ch)]
    (println (str "Message: " msg))
    (recur)))

(crawl {:url "http://bjeanes.com/2012/09/motivate-your-lazy-sequences"
        :search-word "function"
        :report-channel rep-ch
        :depth 2})
;(defn init []
;  (let [center]))
