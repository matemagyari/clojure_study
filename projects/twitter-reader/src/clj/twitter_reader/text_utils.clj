(ns twitter-reader.text-utils
  "Provides utility functions for text manipulation"
  (:require [clojure.set :as set]
            [clojure.string :as str]))

(defn remove-punctuation [text]
  "Removes all non-alphanumeric characters, aside from single quotation mark ('), from text."
  (str/replace text #"(?i)[^\w']+" " "))

(defn text->words
  "Splits the text to a set of words"
  [text]
  (as-> text $
    (remove-punctuation $)
    (str/split $ #"\s")
    (map str/lower-case $)
    (set $)))

(defn seqs->frequencies
  "Returns occurences of elements in a sequence of sequences"
  [seqs]
  (frequencies (mapcat vec seqs)))

(defn reduce-text
  "Reduce text to the interesting words"
  [text search-words]
  (set/intersection (text->words text) search-words))


