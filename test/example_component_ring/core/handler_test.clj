(ns example-component-ring.core.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [example-component-ring.core.handler :refer :all]))

(deftest test-app
  (is (= 1 1)))
