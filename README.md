# stracket

A Clojure library designed to make using JaCoP easier.

JaCoP is a constraint programming solver. 
See http://jacop.osolpro.com/ and https://github.com/radsz/jacop for more info.


From ArchFriends.java in the JaCoP examples.

> Harriet, upon returning from the mall, is happily describing her
> four shoe purchases to her friend Aurora. Aurora just loves the four
> different kinds of shoes that Harriet bought (ecru espadrilles,
> fuchsia flats, purple pumps, and suede sandals), but Harriet can't
> recall at which different store (Foot Farm, Heels in a Handcart, The
> Shoe Palace, or Tootsies) she got each pair. Can you help these two
> figure out the order in which Harriet bought each pair of shoes, and
> where she bought each?

```clojure
(ns stracket.samples
  (require [stracket.core :as s]
           [stracket.search :as ss]
           [stracket.constraint :as sc]))

(def jacop-store (s/store))

(s/defvars shoes
   {:store jacop-store :min 1 :max 4}
   :EcruEspadrilles :FuchsiaFlats :PurplePumps :SuedeSandals)
  
(s/defvars stores
   {:store jacop-store :min 1 :max 4}
   :FootFarm :HeelsInAHandcart :TheShoePlace :Tootsies)

(s/defconstraints constraints

   ;; Each shoe, shop have to have a unique identifier
   (sc/all-different (vals shoes))   
   (sc/all-different (vals stores))

   ;; 1. Harriet bought fuchsia flats at Heels in a Handcart.
   (sc/eq (:FuchsiaFlats shoes) (:HeelsInAHandcart stores))

   ;; 2. The store she visited just after buying her purple pumps was not Tootsies.
   (sc/notc (sc/x+c=z (:PurplePumps shoes) 1 (:Tootsies stores)))

   ;; 3. Foot Farm was Harriet's second stop.
   (sc/eq (:FootFarm stores) 2)

   ;; 4. Two stops after leaving The Shoe Place, Harriet bought her suede sandals.
   (sc/x+c=z (:TheShoePlace stores) 2 (:SuedeSandals shoes)))

(s/impose! jacop-store constraints)
(ss/search-all-at-once jacop-store) 

(println (s/extract-var-info jacop-store))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Produces a solution. 
;; Harriet bought:
;;    EcruEspadrilles at FootFarm
;;    FuchsiaFlats at HeelsInAHandcart
;;    PurplePumps at TheShoePlace
;;    SuedeSandals at Tootsies  
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
{:EcruEspadrilles 2
 :FuchsiaFlats 4
 :PurplePumps 1
 :SuedeSandals 3
 :FootFarm 2
 :HeelsInAHandcart 4
 :TheShoePlace 1
 :Tootsies 3}
```

### This is work in progress.  


## License

Copyright © 2013 Amar Mehta

Distributed under the Eclipse Public License, the same as Clojure.
