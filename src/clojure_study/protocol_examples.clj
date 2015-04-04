(ns
  ^{:author mate.magyari}
  clojure-study.protocol-examples)

(defprotocol House
  (get-characters [this house room])
  (which-room [this house character])
  (put-in-room [this house character room]))

(def house-manager
  (reify House
    (get-characters [this house room]
      (get-in house [:rooms room]))
    (which-room [this house character]
      (get-in house [:characters character]))
    (put-in-room [this house character room]
      (-> house
        (assoc-in [:characters character] room)
        (update-in [:rooms room] conj character)))))

(println
  (put-in-room house-manager {} :jack :room-a))


(def house {:rooms {:room-a [:joe :jack]
                    :room-b [:jane]}
            :characters {:joe :room-a
                         :jack :room-a
                         :jane :room-b}})

(println
  (get-characters house-manager house :room-a)
  "\n"
  (which-room house-manager house :jack))
