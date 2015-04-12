(ns
  ^{:author mate.magyari}
  mini-projects.blackjack.app.lockable)

(defprotocol Lockable
  (acquire-lock! [this id])
  (release-lock! [this id]))

(defmacro with-lock [id repository body]
  `(let [lock# (acquire-lock! ~repository ~id)]
     (if-not lock# (throw (RuntimeException. "No lock available"))
       (do
         (let [result# ~body]
           (release-lock! ~repository ~id)
           result#)))))