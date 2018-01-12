(defproject clojure-study-parent/car-factory "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src"]
  :test-paths   ["test"]
  :plugins [[lein-modules "0.3.11"]]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/test.check "0.6.1"]
                 [org.clojure/core.match "0.2.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojure/core.async "0.1.319.0-6b1aca-alpha"]
                 [io.reactivex/rxclojure "1.0.0"]]
  :main carfactory.factory-async)
