(ns stracket.search
  (:import [JaCoP.search
            Search DepthFirstSearch
            SelectChoicePoint InputOrderSelect
            IndomainMin]))

(defn min-domain
  "Creates an instance of IndomainMin"
  []
  (IndomainMin.))

(defn depth-first-search
  "Create a new depth first search instance"
  []
  (DepthFirstSearch.))

(defn input-order-select
  "Input order selector of variables."
  [store vars domain]
  (InputOrderSelect. store (into-array vars) domain))

(defn labeling
  "Perform a search labeling store and the selection strategy.
  Returns true if there's a solution, otherwise false."
  [search store select]
  (.labeling search store select))

  
