(ns twitter-reader.tweet-processor-test
  (:require [clojure.test :as test]))

(defn is= [a b]
  (test/is (= a b)))

(test/deftest seq-of-sets->frequencies-tests
  (test/are [x y] (= x y)
    (seqs->frequencies [#{:a :b} #{:b :c}]) {:a 1 :b 2 :c 1}
    (seqs->frequencies []) {}))


(test/deftest process-tweet-tests
  (let [search-words #{"a" "b" "c"}
        now (System/currentTimeMillis)
        tweet-1-old {:words #{"a" "b"} :timestamp (- now 60001)}
        tweet-2 {:words #{"b" "c" "e"} :timestamp (- now 20001)}
        tweet-text "a.b c, d e, f"
        new-tweet {:words #{"a" "b" "c"} :timestamp now}
        input {:text tweet-text
               :tweets [tweet-1-old tweet-2]
               :search-words search-words
               :now now}
        result {:tweets [new-tweet tweet-2 ]
                :word-frequencies {"a" 1 "b" 2 "c" 2 "e" 1}}]
    (is= result (process-tweet input))))

(test/run-all-tests)


