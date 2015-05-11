(ns
  ^{:author mate.magyari}
  worldbank.world-bank
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [worldbank.display :as display]))

(defn get-indicators [num] (client/get (str "http://api.worldbank.org/en/indicator"
                                         "?format=json"
                                         "&per_page=" num)
                             {:as :json}))

(defn gdp-indicators [indicators]
  (let [i-list (-> indicators :body second)]
    (filter (fn [i] (.contains (:name i) "GDP")) i-list)))

(comment
  (def inds (get-indicators 15000))
  (def gdp-is (gdp-indicators inds))

  (doseq [x (map (juxt :id :name) gdp-is)]
    (println x))
  )

(defn get-raw-data [indicator country [start-date end-date]]
  (client/get (str "http://api.worldbank.org/countries/" country "/indicators/" indicator "?date=" start-date ":" end-date
                "&format=json"
                "&per_page=1000")
    {:as :json}))


(defn http-response->data [resp]
  (-> resp :body second))

(defn values-dates [response-values]
  (map #(select-keys % [:value :date]) response-values))

(defn time-value-series [response] (-> response http-response->data values-dates))



(def x (time-value-series
         (get-raw-data "NY.GDP.MKTP.CD" "ro;hun" ["2010" "2014"])))

(display/print-histogram x)
(comment

  (json/pprint (client/get (str "http://api.worldbank.org/countries"
                             "?format=json"
                             "&per_page=1000")
                 {:as :json}) )

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
