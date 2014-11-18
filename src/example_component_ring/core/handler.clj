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
    []
  component/Lifecycle

  (start [this]
    (let [routes (routes
                  (GET "/" [] "Schnuffel")
                  (route/not-found "Page not found"))])

    (assoc this :routes routes))

  (stop [this]
    (assoc this :routes nil))

  IRoutesDescriber
  (get-routes [this] (:routes this)))

(defn create-system []
  (component/system-map
   :app (map->Routes {})

   :http-server (component/using
                 (jetty-server {:app (get-routes app)} {:port 3000})
                 [:app])))
