(ns blackjack.util.shared)

(defn generate-id []
  "Generate a new uuid"
  (str (java.util.UUID/randomUUID)))

(defn raise-domain-exception [msg]
  "Throws a domain exception"
  (throw (java.lang.RuntimeException. msg)))

(defn seq-contains? [coll target] 
  "Collection contains target?"
  (some #(= target %) coll))

(defn abs [x]
  {:pre [(number? x)]}
  (if (pos? x)
    x
    (- 0 x)))



