(defproject clojure-study-parent/twitter-reader "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj"]
  :test-paths   ["test/clj"]
  :plugins [[lein-modules "0.3.11"]]
  :dependencies [[org.clojure/clojurescript "0.0-3255"]
                 [org.clojure/clojure "1.7.0-alpha1"]
                 [org.clojure/core.async "0.1.319.0-6b1aca-alpha"]
                 [twitter-api "0.7.8"]
                 [org.twitter4j/twitter4j-stream "4.0.3"]
                 [ring/ring-json "0.2.0"]
                 [http-kit "2.0.0"]
                 [ring/ring-devel "1.1.8"]
                 [compojure "1.1.5"]
                 [ring-cors "0.1.0"]]
  :main twitter-reader.web)
