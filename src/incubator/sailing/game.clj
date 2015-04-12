(ns
  ^{:author mate.magyari}
  sailing.game)

(defn seq-contains? [coll target]
  "Collection contains target?"
  (not= nil (some #(= target %) coll)))

(def flashcards
  [{:id 1
    :text "Sotetben veszteglo komp"
    :images {:daylight ["svkomp_d1.jpg" "svkomp_d2.jpg"]
             :night ["svkomp_n1.jpg"]}}
   {:id 2
    :text "Maganyos vitorlas"
    :images {:daylight ["vit_d1.jpg" "vit_d2.jpg"]
             :night ["vit_n1.jpg"]}}
   {:id 3
    :text "Tolato kotelek"
    :images {:daylight ["tk_d1.jpg" "tk_d2.jpg"]
             :night ["tk_n1.jpg"]}}
   ])

(defn- other-side [side]
  (condp = side
    :images :text :text :images))

(defn get-images [card]
  (concat
    (get-in card [:images :daylight])
    (get-in card [:images :night])))

(defn correct? [question-side question answer flashcards]
  (let [[question-match? answer-map] (condp = question-side
                                       :text [(fn [card] (= question (:text card)))
                                              (fn [card] (-> card get-images flatten))]
                                       :images [(fn [card] (seq-contains? (get-images card) question))
                                                (fn [card] (get card :text))])
        possible-cards (filter question-match? flashcards)
        possible-answers (flatten (map answer-map possible-cards))]
    (seq-contains? possible-answers answer)))

(println "Rnd text " (rnd-question :text flashcards))
(println "Rnd img " (rnd-question :images flashcards))

(def session (atom nil))

(defn questionare
  "Chooses 'question-num' random elements from 'flashcards' with 'choice-num'
  random companions for each, where each companion list contains the corresponding element "
  [question-num choice-num flashcards]
  (let [shuffled-cards (shuffle flashcards)
        chosen-cards (take question-num shuffled-cards)
        questions (for [c chosen-cards
                        :let [choices (->> shuffled-cards
                                         (filter #(not= % c))
                                         (take (dec choice-num))
                                         (cons c)
                                         shuffle)]]
                    [c choices])]
    questions))

(defn card-view [card side]
  (condp = side
    :text (:text card)
    :images (-> card get-images rand-nth)))

(defn question-view
  "A question is the shown card and the choices for answer"
  [[question choices] side]
  (let [q-view (card-view question side)
        other-s (other-side side)
        c-views (for [c choices] (card-view c other-s))]
    [q-view c-views]))

(defn assert-not [x] (assert (not x)))
(assert-not (correct? :text "Maganyos vitorlas" "svkomp_d1.jpg" flashcards)) ;false
(assert (correct? :text "Sotetben veszteglo komp" "svkomp_d1.jpg" flashcards)) ;true
(assert (correct? :images "svkomp_d1.jpg" "Sotetben veszteglo komp" flashcards)) ;true
(assert-not (correct? :images "svkomp_d1.jpg" "Maganyos vitorlas" flashcards)) ;false



