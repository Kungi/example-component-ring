(ns example-component-ring.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.adapter.jetty :as jetty]
            [com.stuartsierra.component :as component]

            [example-component-ring.core.comcomp
             :refer [defroutes-with-deps get-routes IRoutesDescriber]]))


(defroutes-with-deps AppRoutes
  [schnuff]

  (GET "/" [] "Hello World"))

(defrecord Schnuff
    []
  component/Lifecycle

  (start [this]
    (assoc this :schnuff :schnuff))

  (stop [this]
    (assoc this :schnuff nil)))

(defrecord WebappComponent
    [app-routes]
  component/Lifecycle

  (start [this]
    (let [handler
          (wrap-defaults (get-routes app-routes) site-defaults)]

      (assoc this
        :http-server (jetty/run-jetty handler {:port 3000 :join? false}))))

  (stop [this]
    (when-not (nil? (:http-server this))
      (.stop (:http-server this)))

    (assoc this :http-server nil)))


(defn create-system []
  (component/system-map
   :schnuff (map->Schnuff {})
   :app-routes (component/using (map->AppRoutes {})
                                [:schnuff])

   :web-app (component/using (map->WebappComponent {})
                             [:app-routes])))
