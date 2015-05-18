(ns twitter-reader.tweet-processor
  "Processes text and maintaines the tweet buffer"
  (:require [clojure.set :as set]
            [clojure.string :as str]))

(defn- remove-punctuation [text]
  "Removes all non-alphanumeric characters, aside from single quotation mark ('), from text."
  (str/replace text #"(?i)[^\w']+" " "))

(defn- text->words
  "Splits the text to a set of words"
  [text]
  (as-> text $
    (remove-punctuation $)
    (str/split $ #"\s")
    (map str/lower-case $)
    (set $)))

(defn- fresh?
  "True if the tweet's timestamp is less than a minute before now"
  [tweet now]
  (> 60000 (- now (:timestamp tweet))))

(defn- seqs->frequencies
  "Returns occurences of elements in a sequence of sequences"
  [seqs]
  (frequencies (mapcat vec seqs)))

(defn- reduce-text
  "Reduce heroku master text to the interesting words"
  [text search-words]
  (set/intersection (text->words text) search-words))

;; ========== PUBLIC ===========================

(defn process-tweet
  "Add the new tweet's text to the buffer, remove old elements and calculate word frequencies"
  [{:keys [text tweets search-words now]}]
  (let [tweet {:words (reduce-text text search-words) :timestamp now}
        tweets (conj
                 (filter #(fresh? % now) tweets) tweet)
        word-frequencies (seqs->frequencies (map :words tweets))]
    {:tweets tweets
     :word-frequencies word-frequencies}))


