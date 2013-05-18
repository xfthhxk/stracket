(ns stracket.constraint
  (:import [JaCoP.constraints
            XneqC XneqY]))

(defn neq 
  "Creates and returns either an XneqC or XneqY instance.
   XneqC is returne if y is a Number."
  [x y]  
  (if (instance? Number y)
    (XneqC. x y)
    (XneqY. x y)))

