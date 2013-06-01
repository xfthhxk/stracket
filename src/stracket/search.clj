(ns stracket.search
  (require [stracket.core :as s])
  (:import [JaCoP.search
            DepthFirstSearch
            IndomainMin InputOrderSelect 
            MostConstrainedStatic
            Search SelectChoicePoint SimpleSelect]))

(defn min-domain
  "Creates an instance of IndomainMin"
  []
  (IndomainMin.))

(defn depth-first-search
  "Create a new depth first search instance"
  []
  (DepthFirstSearch.))

(defn most-constrained-static
  []
  (MostConstrainedStatic.))

(defn input-order-select
  "Input order selector of variables."
  ([store domain] (input-order-select store (s/extract-vars store) domain))
  ([store vars domain]
     (InputOrderSelect. store (into-array vars) domain)))

(defn labeling
  "Perform a search labeling store and the selection strategy.
  Returns true if there's a solution, otherwise false."
  [search store select]
  (.labeling search store select))

  
(defn search-all-at-once
  "Returns the solution listener"
  ([store] (search-all-at-once store (take-while (complement nil?) (.vars store))))
  ([store all-vars]
     (let [select (SimpleSelect. (into-array all-vars) (MostConstrainedStatic.) (IndomainMin.))
           search (depth-first-search)
           solution-listener (.getSolutionListener search)]
       (doto solution-listener
         (.searchAll true)
         (.recordSolutions true))
       (.setAssignSolution search true)
       (labeling search store select)
       solution-listener)))
      
    
        
(defn search
  "Search method based on input order and lexigraphical ordering of values."
  [store]
  (labeling (depth-first-search) store (input-order-select store (min-domain))))

(defn search-most-constrained-static
  [store]
  (let [vars (into-array (s/extract-vars store))
        select (SimpleSelect. vars (most-constrained-static) (min-domain))]
  (labeling (depth-first-search) store select)))