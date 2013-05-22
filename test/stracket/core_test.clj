(ns stracket.core-test
  (:use midje.sweet)
  (require [stracket.core :as s]
           [stracket.search :as ss]
           [stracket.constraint :as sc]))
  
(defn- first-eg
  []
  (let [store (s/store)
        vars (vec (map #(s/int-var store (str "v" %) 1 4) (range 1 5)))
        dfs (ss/depth-first-search)
        select (ss/input-order-select store vars (ss/min-domain))
        constraint-pairs [[0 1] [0 2] [1 2] [1 3] [2 3]]
        constraints (map (fn[[x y]]
                           (sc/neq (vars x) (vars y))) constraint-pairs)]
    (s/impose! store constraints)
    (if (ss/labeling dfs store select)
      vars
      nil)))


(fact "First example from jacop."
  (let [vars (first-eg)
        vals (map #(.value %) vars)]
    
    vals => [1 2 3 1]))


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
