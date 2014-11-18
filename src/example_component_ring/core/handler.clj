(ns example-component-ring.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.adapter.jetty :as jetty]
            [com.stuartsierra.component :as component]
            [ring.component.jetty :refer [jetty-server]]))

(defprotocol IRoutesDescriber
  (get-routes [this]))

(defrecord Routes
    [handler]
  component/Lifecycle

  (start [this]
    (let [r (routes
             (GET "/" [] "Hallo Welt!")
             (route/not-found "Page not found"))]

      (assoc this :handler r)))

  (stop [this]
    (assoc this :handler nil)))

(defn create-system [config]
  (component/system-map
   :app (map->Routes {})
   :http-server (component/using
                 (jetty-server {:port 3000})
                 [:app])))
