(ns book.book)

(defprotocol TestAgent
  (feed-tweets [this tweets])
  (verify-saved content [this content]))

