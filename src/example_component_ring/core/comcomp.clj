(ns example-component-ring.core.comcomp
  (:require [com.stuartsierra.component :as component]
            [compojure.core :as compojure]))

(defn wrapp-with-dep [f deps]
  (fn [req]
    (f (merge req deps))))

(defn make-handler [routes deps]
  (-> routes
      (wrapp-with-dep deps)))

(defprotocol IRoutesDescriber
  (get-routes [this]))

(defn- attach-request [[method url bindings & body ] unique-alias]
  `(~method ~url [~@bindings :as ~unique-alias] ~@body))

(defn- let-dep [[method url bindings & body] deps unique-alias]
  (let [body-with-let
        `(let [ ~@(mapcat (fn [dep] `(~dep (~(keyword dep) ~unique-alias)))
                          deps)]
           ~@body)]
    `(~method ~url ~bindings ~body-with-let)))

(defn- with-dependency [routes deps]
  (let [unique-alias (gensym)]
    (->> (map #(attach-request % unique-alias) routes)
         (map #(let-dep % deps unique-alias)))))

(defmacro defroutes-with-deps[rec-name deps & routes]
  `(defrecord ~rec-name [~@deps]
     component/Lifecycle
     (start [this#]
       (let [keys# (map keyword '~deps)
             dep-map# (zipmap keys# ~deps)

             routes# (compojure/routes ~@(with-dependency routes deps))]

         (assoc this# :routes (make-handler routes# dep-map#))))
     (stop [this#]
       (assoc this# :routes nil))

     IRoutesDescriber
     (get-routes [this#] (:routes this#))))
