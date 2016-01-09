(ns book.travel-agency)

;coming from a webservice
(defn get-recipes []
  [{:name        :shepherdspie
    :ingredients [:meat :salt]}
   {:name        :tikamassala
    :ingredients [:curry :chicken]}])

;coming from our db
(defn get-ingredient-prices []
  {:salad  1
   :salmon 3
   :flour  2})

(defn with-price [recipe prices]
  (let [price (->> (:ingredients recipe)
                   (map #(get prices %))
                   (reduce +))]
    [recipe price]))

(defn find-3cheapests [{:keys [recipes
                               excluded-ingredients
                               ingredient-prices]}] 1)

(defn recommendation [{:keys [get-recipes
                              get-ingredient-prices
                              register-exclusions!] :as dependencies}
                      excluded-ingredients]
  (register-exclusions! excluded-ingredients)
  (find-3cheapests {:recipes              (get-recipes)
                    :ingredient-prices    (get-ingredient-prices)
                    :excluded-ingredients excluded-ingredients}))

(defn- valid-dependencies? [dependencies]
  (every? some? ((juxt :get-recipes :get-ingredient-prices :register-exclusions!) dependencies)))

(defn create-app [dependencies]
  {:pre [(valid-dependencies? dependencies)]}
  (fn [excluded-ingredients]
    (recommendation dependencies excluded-ingredients)))

(defn to-map [seq]
  (into {} (map (fn [x] [x 0]) seq)))

(def test-dependencies
  (let [excludsion-counts (atom {})]
    {:get-ingredient-prices
     (fn []
       {:salad  1
        :salmon 3
        :flour  2})
     :get-recipes
     (fn [] [{:name        :shepherdspie
              :ingredients [:meat :salt]}
             {:name        :tikamassala
              :ingredients [:curry :chicken]}])
     :register-exclusions!
     (fn [ingredients]
       (swap! excludsion-counts #(merge-with + % (to-map ingredients))))}))

(def app (create-app test-dependencies))

(println
  (app [:salmon]))


