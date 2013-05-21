(ns stracket.core-test
  (:use clojure.test)
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


(deftest a-test
  (testing "First example from jacop."
    (let [vars (first-eg)
          vals (map #(.value %) vars)]
      (is (= [1 2 3 1] vals)))))



      
(defn- arch-friends
  []
  (let [store (s/store)
        shoe-names ["EcruEspadrilles" "FuchsiaFlats" "PurplePumps" "SuedeSandals"]
        shop-names ["FootFarm" "HeelsInAHandcart" "TheShoePlace" "Tootsies"]
        fuchsia-flats 1
        purple-pumps 2
        suede-sandals 3
        foot-farm 0
        heels-in-a-handcart 1
        the-shoe-place 2
        tootsies 3
        make-int-var (fn[name] (s/int-var store name 1 4))
        shoes (vec (map make-int-var shoe-names))
        shops (vec (map make-int-var shop-names))
        all-vars (concat shoes shops)]
    (s/impose! store [
                      (sc/all-different shoes) ; each shoe, shop have to have unique identifiers
                      (sc/all-different shops)
                      (sc/eq (shoes fuchsia-flats) (shops heels-in-a-handcart)) ; harriet bought fucsia flats at heels in a handcart
                      (sc/notc (sc/x+c=z (shoes purple-pumps) 1 (shops tootsies))) ; store after buying purple pumps wasn't Tootsies
                      (sc/eq (shops foot-farm) 2) ; Foot Farm was Harriet's second stop
                      (sc/x+c=z (shops the-shoe-place) 2 (shoes suede-sandals))]) ; 2 stops after The Shoe Place harriet bought suede sandals

    (ss/search-all-at-once store all-vars)
    all-vars))

        
(deftest arch-friends-test
  (testing "ArchFriends test"
    (let [vars (arch-friends)
          ans-map (reduce #(assoc %1 (.id %2) (.value %2)) {} vars)
          ref-map {"EcruEspadrilles" 2
                   "FuchsiaFlats" 4
                   "PurplePumps" 1
                   "SuedeSandals" 3
                   "FootFarm" 2
                   "HeelsInAHandcart" 4
                   "TheShoePlace" 1
                   "Tootsies" 3}]
      (is (= ref-map ans-map)))))

(deftest defvars-test
  (testing "defvars"
    
    (def jacop-store (s/store))
    (s/defvars shoes
      {:store jacop-store :min 1 :max 4}
      :Heels :Flats :Boots :Pumps)
    
  (is (map? shoes))))
