(ns stracket.core
  (:import [JaCoP.core Store IntVar])
  (:require [stracket.constraint :as sc]))


(defn impose
  "Imposes a constraint on to the store"
  [store constraint]
  (.impose store constraint))

(defn int-var
  "Creates a new IntVar instance"
  ([] (IntVar.))
  ([store] (IntVar. store))
  ([store domain] (IntVar. store domain))
  ([store min max] (IntVar. store min max))
;  ([store name] (IntVar. store name))
;  ([store name domain] (IntVar. store name domain))
  ([store name min max] (IntVar. store name min max)))

