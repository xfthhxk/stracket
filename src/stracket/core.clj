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


(defn extract-var-info
  "Returns a map of all var ids and values associated with the store.
   Accepts keyword :as-string to return the ids as strings, otherwise ids are keywords"
  [store & more]
  (let [tr-fn (if (some (partial = :as-string) more) identity keyword)
        reducer-fn (fn[m v]
                     (assoc m (tr-fn (.id v)) (.value v)))
        vars (take-while (complement nil?) (.vars store))] ; array may have 4 entries but length might be > 4 for example
    (reduce reducer-fn {} vars)))



(defn fd-vars
  "Creates a map of keywords to IntVar instances.
   defaults: a map with keys: :store :min :max
   var-names: collects up all the remaining items which are assumed to be
              keywords which will act as the name for an IntVar and also
              the key to lookup the IntVar in the returned map.

    (def jacop-store (store))

    (fd-vars shoes
      {:store jacop-store :min 1 :max 4}
      :Heels :Flats :Boots :Pumps)

    ;; Should output:
    {:Heels (IntVar. store \"Heels\" 1 4)
     :Flats (IntVar. store \"Flats\" 1 4)
     :Boots (IntVar. store \"Boots\" 1 4)
     :Pumps (IntVar. store \"Pumps\" 1 4)}"
  [defaults & var-names]
  (let [{:keys [store min max]} defaults
        vars (map #(int-var store (name %) min max) var-names)]
    (zipmap var-names vars)))


(defmacro defvars
  "Defines a var which is a map of keywords to IntVar instances.
   form-name: the top level var to associate the map to
   Uses the fd-vars function for the crux of the work.
    e.g.:
    (def jacop-store (store))

    (defvars shoes
      {:store jacop-store :min 1 :max 4}
      :Heels :Flats :Boots :Pumps)
  "
  [form-name defaults & var-names]
  `(def ~form-name (fd-vars ~defaults ~@var-names)))

(defmacro defconstraints
  "Defines a var which is a vector of constraints"
  [form-name & constraints]
  (let [c (vec constraints)]
    `(def ~form-name ~c)))


  