(defproject engraver-beginner-example "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[aero "0.1.5" :exclusions [prismatic/schema]]
                 [cheshire "5.5.0"]
                 [mysql/mysql-connector-java "5.1.18"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.cli "0.3.3"]
                 [org.onyxplatform/onyx "0.8.11"]
                 [org.onyxplatform/onyx-kafka "0.8.11.1"]]
  :profiles {:uberjar {:aot :all
                       :uberjar-name "engraver-beginner-example-standalone.jar"}
             :dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [lein-project-version "0.1.0"]]
                   :source-paths ["src"]}}
  :plugins [[lein-project-version "0.1.0"]])
