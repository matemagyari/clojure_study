(ns
  ^{:author mate.magyari}
  clojure_study.other.line-counter)


(defn count-lines-in-file [file-path]
  (with-open [rdr (clojure.java.io/reader file-path)]
    (-> rdr line-seq count)))

(defn files-under [dir extension]
  (let [relevant? (fn [file]
                    (.endsWith
                      (.getName file)
                      (str "." extension)))]
    (->> dir
      (clojure.java.io/file)
      (file-seq)
      (filter relevant?))))

(defn count-lines [dir extension]
  (let [files (files-under dir extension)
        line-counts (map count-lines-in-file files)]
    (reduce + line-counts)))

(def all (count-lines "/Users/mate.magyari/IdeaProjects/clojure_study/src/marsrovers" "clj"))
(def pure (count-lines "/Users/mate.magyari/IdeaProjects/clojure_study/src/marsrovers/pure" "clj"))
(def glue (count-lines "/Users/mate.magyari/IdeaProjects/clojure_study/src/marsrovers/glue.clj" "clj"))
(def app (count-lines "/Users/mate.magyari/IdeaProjects/clojure_study/src/marsrovers/app/app.clj" "clj"))

(println "All:" all)
(println "Pure:" pure (double (/ pure all)))
(println "glue:" glue (double (/ glue all)))
(println "app:" app (double (/ app all)))

(count-lines "/Users/mate.magyari/IdeaProjects/clojure_study/src/marsrovers/pure/plateau.clj" "clj")
(count-lines "/Users/mate.magyari/IdeaProjects/clojure_study/src/marsrovers/app/app.clj" "clj")
(count-lines "/Users/mate.magyari/IdeaProjects/clojure_study/src/marsrovers/glue.clj" "clj")
(count-lines "/Users/mate.magyari/IdeaProjects/clojure_study/src/marsrovers/api" "clj")

(count-lines "/Users/mate.magyari/IdeaProjects/others/marsrover/marsrover/src/main/scala/org/kaloz/excercise/marsrover" "scala")
(count-lines "/Users/mate.magyari/IdeaProjects/others/marsrover/marsrover/src/main/scala/org/kaloz/excercise/marsrover/Plateau.scala" "scala")
(count-lines "/Users/mate.magyari/IdeaProjects/others/marsrover/marsroverapi" "scala")






