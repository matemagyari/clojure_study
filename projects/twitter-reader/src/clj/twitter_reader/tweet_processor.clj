(ns twitter-reader.tweet-processor
  "Processes text and maintaines the tweet buffer"
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [twitter-reader.text-utils :as tu]))

(defn- fresh?
  "True if the tweet's timestamp is less than a minute before now"
  [tweet now]
  (> 60000 (- now (:timestamp tweet))))

;; ========== PUBLIC ===========================

(defn process-tweet
  "Add the new tweet's text to the buffer, remove old elements and calculate word frequencies"
  [{:keys [text tweets search-words now]}]
  (let [tweet {:words (tu/reduce-text text search-words) :timestamp now}
        tweets (conj
                 (filter #(fresh? % now) tweets) tweet)
        word-frequencies (tu/seqs->frequencies (map :words tweets))]
    {:tweets tweets
     :word-frequencies word-frequencies}))


