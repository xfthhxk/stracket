(ns stracket.core-test
  (:use midje.sweet)
  (require [stracket.core :as s]
           [stracket.search :as ss]
           [stracket.constraint :as sc]))
  
(fact "JaCoP map coloring example"
  (let [store (s/store)
        vars (s/fd-vars {:store store :min 1 :max 4} :V1 :V2 :V3 :V4)
        constraints (map (fn [[x y]](sc/neq (x vars) (y vars)))
                         [[:V1 :V2] [:V1 :V3] [:V2 :V3] [:V2 :V3] [:V3 :V4]])
        dfs (ss/depth-first-search)
        select (ss/input-order-select store (vals vars) (ss/min-domain))]
    (s/impose! store constraints)
    (ss/labeling dfs store select) => true
    (s/extract-var-info store) => {:V4 1, :V3 2, :V2 1, :V1 3}))


(fact "defvars test"
  (def jacop-store (s/store))
      
  (s/defvars shoes
    {:store jacop-store :min 1 :max 4}
    :EcruEspadrilles :FuchsiaFlats :PurplePumps :SuedeSandals)
  
  (s/defvars stores
    {:store jacop-store :min 1 :max 4}
    :FootFarm :HeelsInAHandcart :TheShoePlace :Tootsies)

  jacop-store => s/store?
  
  shoes => map?
  (:FuchsiaFlats shoes) => s/int-var?
  (-> shoes :FuchsiaFlats .min) => 1
  (-> shoes :FuchsiaFlats .max) => 4

  stores => map?
  (:FootFarm stores) => s/int-var?
  (-> stores :FootFarm .min) => 1
  (-> stores :FootFarm .max) => 4)


(fact "defconstraints test"
  (s/defconstraints constraints
    (sc/all-different (vals shoes))
    (sc/all-different (vals stores))
    (sc/eq (:FuchsiaFlats shoes) (:HeelsInAHandcart stores))
    (sc/notc (sc/x+c=z (:PurplePumps shoes) 1 (:Tootsies stores)))
    (sc/eq (:FootFarm stores) 2)
    (sc/x+c=z (:TheShoePlace stores) 2 (:SuedeSandals shoes)))

  constraints => vector?)

(fact "test Arch Friends result"
  (s/impose! jacop-store constraints)
  (ss/search-all-at-once jacop-store) ;; this mutates the vars created with defvars      
  (s/extract-var-info jacop-store) => {:EcruEspadrilles 2
                                       :FuchsiaFlats 4
                                       :PurplePumps 1
                                       :SuedeSandals 3
                                       :FootFarm 2
                                       :HeelsInAHandcart 4
                                       :TheShoePlace 1
                                       :Tootsies 3})

(fact "test fd-vars macro"
  (let [j-store (s/store)
        shoes-map (s/fd-vars {:store jacop-store :min 1 :max 4}
                             :EcruEspadrilles :FuchsiaFlats :PurplePumps :SuedeSandals)]
    shoes-map => map?
    (:SuedeSandals shoes-map) => s/int-var?
    (-> shoes-map :SuedeSandals .min) => 1
    (-> shoes-map :SuedeSandals .max) => 4))

