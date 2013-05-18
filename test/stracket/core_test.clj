(ns stracket.core-test
  (:use clojure.test)
  (require [stracket.core :as s]
           [stracket.constraint :as sc]
           [stracket.search :as ss]))

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


      
      
      
          

          
          

