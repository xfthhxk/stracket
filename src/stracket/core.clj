(ns stracket.core
  (:import [JaCoP.core
            BooleanVar
            Store IntVar])
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
  ([]
     "Creates a new IntVar instance"     
     (IntVar.))
  
  ([store name domains]
     "Creates a new IntVar instance associated with the specified store and name.
     'domains' is a seq of pairs ie [[1 5] [10 20]]"             
     (let [var (IntVar. store name)]
       (doseq [[start end] domains]
         (.addDom var start end))
       var)))

(defn boolean-var
  ([]
     "Creates a new BooleanVar instance."
     (BooleanVar.))
  ([store name]
     "Creates a new BooleanVar instance with the specified store and name."
     (BooleanVar. store name)))

