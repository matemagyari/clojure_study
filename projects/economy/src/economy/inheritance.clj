(ns economy.inheritance
  (:require [clojure.spec :as s]
            [clojure.spec.test :as stest]]))

(def employee-types #{:office-worker :field-agent})
(s/def ::type employee-types)
(s/def ::years (s/int-in 0 100))
(s/def ::name string?)
(s/def ::base-salary (s/int-in 0 1000))
(s/def ::physical-work? boolean?)
(s/def ::employee (s/keys :req [::years ::name ::base-salary ::type]
                          :opt [::physical-work?]))

(s/fdef salary
        :args (s/cat :in ::employee)
        :ret number?)

(defmulti years-contribution ::type)

(defmethod years-contribution :office-worker [x]
  (* 30 (::years x)))

(defmethod years-contribution :field-agent [x]
  (let [multiplier (if (::physical-work? x) 40 30)]
    (* multiplier (::years x))))

(defn to-string [employee]
  (str "My name is " (::name employee) " and I've been working here for " (::years employee) " years."))

(defn office-worker [name years]
  {::name name ::years years ::base-salary 800 ::type :office-worker})

(defn field-agent [name years physical-work?]
  {::name name ::years years ::base-salary 1000
   ::physical-work? physical-work? ::type :field-agent})

(defn salary [employee]
  (+ (::base-salary employee) (years-contribution employee)))

(defn run []
  (let [joe (field-agent "joe" 17 false)
        jil (field-agent "jil" 17 true)
        jack (office-worker "jack" 20)]
    (doseq [x [joe jil jack]]
      (println (to-string x))
      (println (salary x)))))



