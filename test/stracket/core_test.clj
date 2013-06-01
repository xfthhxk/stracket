(ns stracket.core-test
  (:use midje.sweet)
  (require [stracket.core :as s]
           [stracket.search :as ss]
           [stracket.constraint :as sc]
           [stracket.etc :as etc]))

(fact "test boolean var"
      (s/boolean-var (s/store) "TestVar") => s/boolean-var?
      (s/boolean-var (s/store) "TestVar" 0 1) => s/boolean-var?)

(fact "test fd-vars fn for boolean var creation"
      (let [j-store (s/store)
            vars (s/fd-vars {:store j-store :min 0 :max 1 :type :boolean}
                            :bool-var1 :bool-var2)]
        (:bool-var1 vars) => s/boolean-var?))

(fact "test fd-vars fn for int var creation"
      (let [j-store (s/store)
            vars (s/fd-vars {:store j-store :min 10 :max 20}
                            :int-var1 :int-var2)]
        (:int-var1 vars) => s/int-var?))
                            

(fact "test fd-vars fn"
  (let [j-store (s/store)
        shoes-map (s/fd-vars {:store j-store :min 1 :max 4}
                             :EcruEspadrilles :FuchsiaFlats :PurplePumps :SuedeSandals)]
    shoes-map => map?
    (:SuedeSandals shoes-map) => s/int-var?
    (-> shoes-map :SuedeSandals .min) => 1
    (-> shoes-map :SuedeSandals .max) => 4))
  
(fact "JaCoP map coloring example"
  (let [store (s/store)
        vars (s/fd-vars {:store store :min 1 :max 4} :V1 :V2 :V3 :V4)
        constraints (map (fn [[x y]](sc/neq (x vars) (y vars)))
                         [[:V1 :V2] [:V1 :V3] [:V2 :V3] [:V3 :V4]])
        dfs (ss/depth-first-search)
        select (ss/input-order-select store (vals vars) (ss/min-domain))]
    (s/impose! store constraints)
    (ss/labeling dfs store select) => true
    (s/extract-var-info store) => {:V4 1 :V3 2 :V2 1 :V1 3}))


(facts "ArchFriends test"
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
               (sc/not= (sc/x+c=z (:PurplePumps shoes) 1 (:Tootsies stores)))
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
                                                  :Tootsies 3}))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Solution(s) found
;; 1 1 1 0 0 0 0
;; 1 0 0 1 1 0 0
;; 1 0 0 0 0 1 1
;; 0 1 0 1 0 1 0
;; 0 1 0 0 1 0 1
;; 0 0 1 1 0 0 1
;; 0 0 1 0 1 1 0
;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(future-fact "BIBD test"
      (let [bool-var-names (for [i (range 7)
                                 j (range 7)]
                             (keyword (str "x" i "_" j)))
            store (s/store)
            bool-vars (s/fd-vars {:store store :min 0 :max 1 :type :boolean}
                                 bool-var-names)
            v 7
            b 7
            r 3
            k 3
            lambda 1
            r-var (s/int-var store "r" r r)
            k-var (s/int-var store "k" k k)
            lambda-var (s/int-var store "lambda" lambda lambda)
            ordered-bool-vars (map #(-> % keyword bool-vars) bool-var-names)
            shaped-bool-vars (etc/shape ordered-bool-vars v b)]

        (s/impose! store (map #(sc/sum= (etc/extract-row shaped-bool-vars %) r-var) (range v)) 1)
        (s/impose! store (map #(sc/sum= (etc/extract-col shaped-bool-vars %) k-var) (range k)) 1)))
        
        
(fact "Baby sitting test"
      (let [store (s/store)
            surnames (s/fd-vars {:store store :min 2 :max 6}        ;; values 2 - 6 are ages of the children
                                :Fell :Grant :Hall :Ivey :Jule)
            first-names (s/fd-vars {:store store :min 2 :max 6}
                                   :Keith :Libby :Margo :Nora :Otto)]
        

        (s/impose! store [(sc/all-different (vals surnames))         ;; Each person has to have a different surname and different first name
                          (sc/all-different (vals first-names))
                          (sc/eq (:Libby first-names) (:Jule surnames))         ;; 1. One child is named Libby Jule
                          (sc/x+c=z (:Ivey surnames) 1 (:Keith first-names))    ;; 2. Keith is one year older than Ivey
                          (sc/x+c=z (:Nora first-names) 1 (:Ivey surnames))     ;; 3. Ivey is one year older than Nora
                          (sc/x+c=z (:Margo first-names) 3 (:Fell surnames))    ;; 4. Fell is three years older than Margo
                          (sc/x*c=z (:Hall surnames) 2 (:Otto first-names))])   ;; 5. Otto is twice as old as Hall
        (ss/search store)

        (s/extract-var-info store) => {:Margo 2 :Hall 2
                                       :Nora 3 :Grant 3
                                       :Otto 4 :Ivey 4
                                       :Keith 5 :Fell 5
                                       :Libby 6 :Jule 6}))


(fact "Basic logic pascal"
      (let [letters (map (comp keyword str) (distinct "basiclogicpascal")) ;; each distinct letter has an associated FD var
            store (s/store)
            digits (s/fd-vars {:store store :min 0 :max 9} letters)
            basic (map digits [:b :a :s :i :c])
            logic (map digits [:l :o :g :i :c])
            pascal (map digits [:p :a :s :c :a :l])
            value-basic (s/int-var store "v-BASIC" 0 99999)
            value-logic (s/int-var store "v-LOGIC" 0 99999)
            value-pascal (s/int-var store "v-PASCAL" 0 999999)
            weight-generator (fn[n] (map #(Math/pow 10 %) (-> n range reverse)))]
        
        (s/impose! store [(sc/all-different (vals digits))  ;; each letter has a unique value
                          (sc/sum-weight= basic (-> basic count weight-generator) value-basic)
                          (sc/sum-weight= logic (-> logic count weight-generator) value-logic)
                          (sc/sum-weight= pascal (-> pascal count weight-generator) value-pascal)
                          (sc/x+y=z value-basic value-logic value-pascal)  ;; main equation of BASIC + LOGIC = PASCAL
                          (sc/neq (first basic) 0)     ;; Since B is the first digit of BASIC
                          (sc/neq (first logic) 0)     ;; and L ist the first digit of LOGIC or PASCAL
                          (sc/neq (first pascal) 0)])  ;; both letters cannot be equal to zero

        (ss/search-most-constrained-static store)
        (s/extract-var-info store) => {:b 6 :a 0 :s 8 :i 5 :l 4 :o 7 :g 3 :c 2 :p 1 :v-BASIC 60852 :v-LOGIC 47352 :v-PASCAL 108204}))
                          
                          
                          
            





                   
            
            
