(ns twitter-reader.tweet-processor
  (:require [clojure.set :as set]
            [clojure.string :as str]))

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

;; ========== PUBLIC ===========================

(defn process-tweet
  "Updates the statistics with the new tweet"
  [{:keys [tweet-text stats search-words] :as input}]
  (let [found-words (set/intersection (text->words tweet-text) search-words) ; reduce tweet to the interesting words
        found-words-map (reduce #(assoc %1 %2 1) {} found-words)
        word-occurrences (merge-with + found-words-map (:word-occurrences stats))
        tweet-count (:tweet-count stats)]
    (assoc stats
      :word-occurrences word-occurrences
      :tweet-count (inc tweet-count))))



