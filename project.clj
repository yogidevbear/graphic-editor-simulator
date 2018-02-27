(defproject graphic-editor-simulator "0.1.0-SNAPSHOT"
  :description "A fun project written in Clojure to simulate functionality of a graphical editor."
  :url "https://github.com/yogidevbear/graphic-editor-simulator"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.rpl/specter "1.1.0"]]
  :main ^:skip-aot sim.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
