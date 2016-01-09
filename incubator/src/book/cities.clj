(ns book.cities)

(def land-map
  {:city1 [:city2 :city3]
   :city2 [:city3]
   :city3 :city2})

(defrecord city [id humans])

(defn neighbours [city]
  (city land-map))

(defn move [human city-from city-to])
