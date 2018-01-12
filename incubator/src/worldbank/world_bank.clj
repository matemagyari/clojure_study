(ns worldbank.world-bank
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [worldbank.display :as display]))

(def url-prefix "http://api.worldbank.org")

(defn read-url [url]
  (client/get
    (str url-prefix url)
    {:as :json}))

(defn get-indicators [num]
  (read-url (str "/en/indicator?format=json&per_page=" num)))

(defn gdp-indicators [indicators]
  (let [gdp? (fn [i] (.contains (:name i) "GDP"))
        i-list (-> indicators :body second)]
    (filter gdp? i-list)))

(defn get-indicators2 []
  (->> (gdp-indicators 1500)
    :body
    second
    (sort-by :name)
    (map #((juxt :name :id) %))
    (map println)))

(comment
  (def inds (get-indicators 15000))
  (def gdp-is (gdp-indicators inds))

  (doseq [x (map (juxt :id :name) gdp-is)]
    (println x))
  )

(defn get-raw-data [indicator country [start-date end-date]]
  (client/get
    (str "http://api.worldbank.org/countries/" country "/indicators/" indicator
         "?date=" start-date ":" end-date
         "&format=json"
         "&per_page=1000")
    {:as :json}))


(defn http-response->data [resp]
  (-> resp :body second))

(defn values-dates [response-values]
  (map #(select-keys % [:value :date]) response-values))

(defn time-value-series [response] (-> response http-response->data values-dates))



(def histogram (time-value-series
         (get-raw-data "NY.GDP.MKTP.CD" "usa" ["1800" "2015"])))

;(display/print-histogram histogram)

(comment

  (json/pprint (client/get (str "http://api.worldbank.org/countries"
                                "?format=json"
                                "&per_page=1000")
                           {:as :json}))

  (def resp
    (client/get (str "http://api.worldbank.org/countries/all/indicators/SP.POP.TOTL?date=2000:2001&format=json"
                     "?format=json"
                     "&per_page=" num)
                {:as :json
                 ;:client-params {"format" "json"}
                 ;:accept :json
                 }))

  (json/pprint resp)
  (println "\nIndicators\n")
  (json/pprint indicators)

  )
