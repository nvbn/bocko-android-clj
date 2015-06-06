(defproject bocko-android-clj/bocko-android-clj "0.0.1-SNAPSHOT"
            :description "FIXME: Android project description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}

            :global-vars {*warn-on-reflection* true}

            :source-paths ["src/clojure" "src"]
            :java-source-paths ["src/java"]
            :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
            :plugins [[lein-droid "0.4.0-alpha2"]]

            :dependencies [[org.clojure-android/clojure "1.7.0-alpha6" :use-resources true]
                           ; Fixes compilation error
                           ;[org.clojure/core.async "0.1.346.0-17112a-alpha"]
                           ;[org.clojure/clojurescript "0.0-3169"]
                           [neko/neko "3.2.0"]]
            :lean
            [:dev
             {:dependencies ^:replace [[org.skummet/clojure-android "1.7.0-alpha5-r2" :use-resources true]
                                       ]
              :exclusions [[org.clojure/clojure]
                           [org.clojure-android/clojure]]
              :jvm-opts ["-Dclojure.compile.ignore-lean-classes=true"]
              :global-vars ^:replace {clojure.core/*warn-on-reflection* true}
              :android {:lean-compile true
                        :skummet-skip-vars ["#'neko.init/init"
                                            "#'neko.context/context"
                                            "#'neko.resource/package-name"
                                            "#'neko.-utils/keyword->static-field"
                                            "#'neko.-utils/keyword->setter"
                                            "#'neko.ui.traits/get-display-metrics"
                                            "#'test.leindroid.sample.main/MainActivity-onCreate"
                                            "#'test.leindroid.sample.main/MainActivity-init"]}}]
            :profiles {:default [:dev]

                       :dev
                       [:android-common :android-user
                        {:dependencies [
                                        ;[org.clojure/tools.nrepl "0.2.10"]
                                        ]
                         :target-path "target/debug"
                         :android {:aot :all
                                   :rename-manifest-package "bockoandroidclj.debug"
                                   :manifest-options {:app-name "BockoAndroidClj - debug"}}}]
                       :release
                       [:android-common
                        {:target-path "target/release"
                         :android
                         {;; Specify the path to your private keystore
                          ;; and the the alias of the key you want to
                          ;; sign APKs with.
                          ;; :keystore-path "/home/user/.android/private.keystore"
                          ;; :key-alias "mykeyalias"

                          :ignore-log-priority [:debug :verbose]
                          :aot :all
                          :build-type :release}}]}

            :android {;; Specify the path to the Android SDK directory.
                      ;; :sdk-path "/home/user/path/to/android-sdk/"

                      ;; Try increasing this value if dexer fails with
                      ;; OutOfMemoryException. Set the value according to your
                      ;; available RAM.
                      :dex-opts ["-JXmx4096M"]

                      ;; If previous option didn't work, uncomment this as well.
                      ;; :force-dex-optimize true

                      :target-version "22"
                      :aot-exclude-ns ["clojure.parallel" "clojure.core.reducers"
                                       "cljs-tooling.complete" "cljs-tooling.info"
                                       "cljs-tooling.util.analysis" "cljs-tooling.util.misc"
                                       "cider.nrepl" "cider-nrepl.plugin"
                                       "cljs.analyzer" "cljs.core.async.macros"
                                       "cljs.core.impl-ioc-macros"]})
