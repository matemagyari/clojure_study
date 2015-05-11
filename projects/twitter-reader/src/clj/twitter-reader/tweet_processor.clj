(ns twitter-reader.tweet-processor
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.core.async :as async]))

;; Tweet Processor Component. Maintains the state the statistics and sends updates to the Display Component

(defn- remove-punctuation [text]
  "Removes all non-alphanumeric characters, aside from single quotation
   mark ('), from text."
  (str/replace text #"(?i)[^\w']+" " "))

(defn- text->words
  "Splits the text to a set of words"
  [text]
  (as-> text $
    (remove-punctuation $)
    (str/split $ #"\s")
    (map str/lower-case $)
    (set $)))

(defn- process-tweet
  "Updates the statistics with the new tweet"
  [{:keys [tweet-text stats search-words] :as input}]
  (let [found-words (set/intersection (text->words tweet-text) search-words)
        found-words-map (reduce #(assoc %1 %2 1) {} found-words)
        word-occurrences (merge-with + found-words-map (:word-occurrences stats))
        tweet-count (:tweet-count stats)]
    (assoc stats
      :word-occurrences word-occurrences
      :tweet-count (inc tweet-count))))

;; ========== PUBLIC ===========================

(defprotocol TweetProcessor
  "An interface to abstract away the tweet processing"
  (handle-tweet! [this tweet] "Processes a tweet"))

(defn create-tweet-processor
  "Factory function. Creates an instance of the TweetProcessor"
  [{:keys [search-words out-channel]}]
  (let [state (atom {:word-occurrences {}
                     :tweet-count 0})]
    (reify TweetProcessor
      (handle-tweet! [this tweet]
        (let [new-state (process-tweet {:tweet-text tweet :stats @state :search-words search-words})]
          (reset! state new-state)
          (async/go
            (async/>! out-channel {:statistics new-state
                                   :text tweet})))))))


