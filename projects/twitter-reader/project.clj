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
                 [org.clojure/test.check "0.6.1"]
                 [org.clojure/core.async "0.1.319.0-6b1aca-alpha"]
                 [twitter-api "0.7.8"]]
  :main twitter-reader.main)
