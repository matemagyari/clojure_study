(ns blackjack.domain.playerground)

(def ^:dynamic buffer (rand 12))
(dotimes [n 5]
  (future (println n buffer)))

(binding [buffer 16]
  (future (println "hi" buffer)))



