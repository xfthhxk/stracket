(ns stracket.search
  (:import [JaCoP.search
            MostConstrainedStatic
            Search DepthFirstSearch
            SelectChoicePoint InputOrderSelect
            IndomainMin
            SimpleSelect]))

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

  
(defn search-all-at-once
  "Returns the solution listener"
  [store all-vars]
  (let [select (SimpleSelect. (into-array all-vars) (MostConstrainedStatic.) (IndomainMin.))
        search (depth-first-search)
        solution-listener (.getSolutionListener search)]
    (doto solution-listener
      (.searchAll true)
      (.recordSolutions true))
    (.setAssignSolution search true)
    (labeling search store select)
    solution-listener))
      
    
        
        