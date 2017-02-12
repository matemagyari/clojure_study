(ns economy.inheritance
  (:require [clojure.spec :as s]
            [clojure.spec.test :as stest]))

(def employee-types #{:manager :developer :hr})
(s/def ::employee-type employee-types)
(s/def ::years nat-int?)
(s/def ::employee (s/keys :req [::years ::employee-type]))


(s/fdef salary
        :args (s/cat :in ::employee)
        :ret double?)

(defmulti multiplier ::employee-type)
(defmethod multiplier :manager [_] 3.0)
(defmethod multiplier :developer [_] 2.0)
(defmethod multiplier :hr [_] 1.0)
(defmethod multiplier :default [_] 0.0)

(defn salary [employee]
  (* (::years employee) (multiplier employee)))

(def man1 {::employee-type :manager
           ::years         14})
(def dev1 {::employee-type :developer
           ::years         6})
(def hr1 {::employee-type :hr
          ::years         12})
