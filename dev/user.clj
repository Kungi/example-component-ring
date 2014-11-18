(ns user
  (:require [clojure.tools.namespace.repl :as repl]
            [com.stuartsierra.component :as component]
            [example-component-ring.core.handler :as app]))

(def system nil)

(defn init []
  (alter-var-root #'system
                  (constantly (app/create-system))))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system
    (fn [s] (when s (component/stop s)))))

(defn go []
  (init)
  (start))

(defn reset []
  (repl/set-refresh-dirs "src")
  (stop)
  (repl/refresh :after 'user/go))
