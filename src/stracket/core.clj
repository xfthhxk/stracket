(ns stracket.core
  (:import [JaCoP.core
            BooleanVar
            Store IntVar])
  (:require [stracket.constraint :as sc]))

(defn store?
  "Answers if the argument is an instance of Store"
  [x]
  (instance? Store x))

(defn int-var?
  "Answers if the argument is an instance of IntVar"
  [x]
  (instance? IntVar x))

(defn boolean-var?
  "Answers if the argument is an instance of BooleanVar"
  [x]
  (instance? BooleanVar x))

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
  ([store name min max] (IntVar. store name min max))
  ([store name domains]
     "Creates a new IntVar instance associated with the specified store and name.
     'domains' is a seq of pairs ie [[1 5] [10 20]]"             
     (let [var (IntVar. store name)]
       (doseq [[start end] domains]
         (.addDom var start end))
       var)))

(defn boolean-var
  ([store name]
     "Creates a new BooleanVar instance with the specified store and name."
     (BooleanVar. store name)))


(defmacro defvars
  "Defines a var which is a map of keywords to IntVar instances.
   form-name: the top level var to associate the map to
   defaults: a map with keys: :store :min :max
   var-names: collects up all the remaining items which are assumed to be
              keywords which will act as the name for an IntVar and also
              the key to lookup the IntVar in the def'd map.

    e.g.:
    (def jacop-store (store))

    (defvars shoes
      {:store jacop-store :min 1 :max 4}
      :Heels :Flats :Boots :Pumps)
  "
  [form-name defaults & var-names]
  (let [{:keys [store min max]} defaults]
    `(let [store# ~store
           vars# (map #(int-var store# (name %) ~min ~max) '~var-names)]
       (def ~form-name (zipmap '~var-names vars#)))))


