(defproject clojure-study-parent "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-modules "0.3.11"]
            [lein-sub "0.2.4"]]
  :sub ["projects/carfactory"
        "projects/twitter-reader"
        "projects/bigdata-aggregator"
        ;"projects/blackjack"
        "projects/swarm"
        ;"clojure_study"
        ;"incubator"
        ]
  :dependencies [[org.clojure/clojure "1.8.0"]])
