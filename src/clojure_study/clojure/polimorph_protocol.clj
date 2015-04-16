(ns clojure-study.clojure.polimorph-protocol
  (:use clojure-study.clojure.assertion))


;;extend existing types with protocol
(defprotocol Whoami
  (whoami [this]))

(extend-protocol Whoami
  java.lang.Boolean
  (whoami [this]
    (str "I'm a boolean: " this))
  clojure.lang.Keyword
  (whoami [this]
    (str "I'm a keyword: " this))
  nil
  (whoami [this]
    "I'm a null"))

(assert= "I'm a boolean: true" (whoami true))
(assert= "I'm a keyword: :a" (whoami :a))
(assert= "I'm a null" (whoami nil))

;;reify
(let [joe
      (reify Whoami
        (whoami [_] "Joe"))]
  (assert= "Joe" (whoami joe)))

;;defprotocol
(defprotocol Dog
  (eat [this])
  (bark [this]))

(defrecord Terrier [])

;;defrecord
(defrecord Car [year color])
(def kit (Car. 1987 :black))

(assert= 1987 (:year kit))

;; extend protocol
(defrecord Person [name age]
  Whoami
  (whoami [this] (str "I am " name ", " age " yrs old")))

(def john18 (Person. "John" 18))
(def jane17 (->Person "Jane" 17))

(assert= "I am John, 18 yrs old" (whoami john18))
(assert= "I am Jane, 17 yrs old" (whoami jane17))

;;defrecord with methods
(defprotocol Person-change
  (year-passed [this])
  (year-passed2 [this])
  (years-passed [this years])
  (me [this]))

(defrecord ChangingPerson [name age]
  Person-change
  (year-passed [this] (ChangingPerson. name (inc age)))
  (year-passed2 [this] (assoc this 
                              :age (inc age)))
  (years-passed [this years] (ChangingPerson. name (+ years age)))
  (me [this] this))

(def Jack (ChangingPerson. "Jack" 22))
(assert= (ChangingPerson. "Jack" 23)
               (year-passed Jack))
(assert= (ChangingPerson. "Jack" 23)
               (year-passed2 Jack))
(assert= (ChangingPerson. "Jack" 32)
               (years-passed Jack 10))
(assert= Jack
               (me Jack))

;;

