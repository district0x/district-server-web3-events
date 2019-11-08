(defproject district0x/district-server-web3-events "1.1.0-SNAPSHOT"
  :description "district0x server module for handling web3 events"
  :url "https://github.com/district0x/district-server-web3-events"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[cljs-web3-next "0.0.15"]
                 [com.taoensso/timbre "4.10.0"]
                 [district0x/district-server-config "1.0.1"]
                 [district0x/district-server-smart-contracts "1.1.0"]
                 [district0x/district-server-web3 "1.1.3"]
                 [medley "1.0.0"]
                 [mount "0.1.16"]
                 [org.clojure/clojurescript "1.10.520"]]

  :plugins [[lein-npm "0.6.2"]
            [lein-doo "0.1.8"]
            [lein-solc "1.0.11"]]

  :npm {:dependencies [[web3 "1.2.0"]]
        :devDependencies [[jsedn "0.4.1"]
                          [ws "2.0.1"]]}

  :clean-targets ^{:protect false} ["target" "tests-compiled"]

  :profiles {:dev {:dependencies [[district0x/async-helpers "0.1.3"]
                                  [district0x/district-server-logging "1.0.6"]
                                  [lein-doo "0.1.8"]
                                  [org.clojure/clojure "1.10.1"]]
                   :source-paths ["test" "src"]}}

  :deploy-repositories [["snapshots" {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]
                        ["releases"  {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]]

  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["deploy"]]

  :cljsbuild {:builds [{:id "nodejs-tests"
                        :source-paths ["src" "test"]
                        :compiler {:main "tests.runner"
                                   :output-to "tests-compiled/run-tests.js"
                                   :output-dir "tests-compiled"
                                   :target :nodejs
                                   :optimizations :none
                                   :source-map true}}]})
