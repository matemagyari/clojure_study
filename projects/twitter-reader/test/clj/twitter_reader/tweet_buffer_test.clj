(ns twitter-reader.tweet-buffer-test
  (:require [clojure.test :as test]
            [twitter-reader.tweet-processor :refer :all]))

(defn is= [a b]
  (test/is (= a b)))

(test/deftest update-buffer-tests
  (let [now (System/currentTimeMillis)
        tweet-1-old {:words #{"a" "b"} :timestamp (- now 60001)}
        tweet-2 {:words #{"b" "c" "e"} :timestamp (- now 20001)}
        new-tweet {:words #{"a" "b" "c"} :timestamp now}
        input {:text "a.b c, d e, f"
               :tweets [tweet-1-old tweet-2]
               :search-words #{"a" "b" "c"}
               :now now}
        result {:tweets [new-tweet tweet-2]
                :word-frequencies {"a" 1 "b" 2 "c" 2 "e" 1}}]
    (is= result (update-buffer input))))


