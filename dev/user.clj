(ns user
  (:require [reloaded.repl :refer [system init start stop go reset]]
            [example-component-ring.core.handler :refer [create-system]]))

(reloaded.repl/set-init! #(create-system {}))
