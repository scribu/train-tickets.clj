(ns user
  (:require [clojure.tools.namespace.repl :refer (refresh refresh-all)]))

(defn reset []
  (refresh))
