(ns stracket.core
  (:import [JaCoP.core Store IntVar])
  (:require [stracket.constraint :as sc]))

(defn store
  "makes a new Store instance"
  []
  (Store.))

(defn impose!
  "Imposes constraints on to the store. The store is mutated."
  [store constraints]
  (doseq [c constraints]  ; doseq to realize potentially lazy constraints
    (.impose store c)))

(defn int-var
  "Creates a new IntVar instance"
  ([] (IntVar.))
  ([store] (IntVar. store))
  ([store domain] (IntVar. store domain))
  ([store min max] (IntVar. store min max))
;  ([store name] (IntVar. store name))
;  ([store name domain] (IntVar. store name domain))
  ([store name min max] (IntVar. store name min max)))

