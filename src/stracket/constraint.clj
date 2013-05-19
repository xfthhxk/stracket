(ns stracket.constraint
  (:import [JaCoP.constraints
            AbsXeqY Alldiff Alldifferent Alldistinct Among AmongVar And AndBool
            Circuit
            Not
            XeqC XeqY XneqC XneqY XplusCeqZ]))

(defn abs-eq
  "Creates and returns an AbsXeqY instance.
   x and y are IntVar instances.
  Constrains |X|#=Y."
  [x y]
  (AbsXeqY. x y))

(defn all-diff
  "Assures all FDVs have different values. Note this is different from all-different.
   'vars' is a seq of IntVars"
  [vars]
  (Alldiff. (into-array vars)))

(defn all-different
  "Assures all FDVs have different values.
   'vars' is a seq of IntVars"
  [vars]
  (Alldifferent. (into-array vars)))

(defn all-distinct
  "Assuers all FDVs have different values.
  'vars' is a seq of IntVars"
  [vars]
  (Alldistinct. (into-array vars)))

(defn among
  "Creates an among constraint.
  'vars' is a coll of IntVars, 'k-set' is an IntervalDomain and 'n' is an IntVar"
  [vars k-set n]
  (Among. (into-array vars) k-set n))

(defn among-var
  "An among constraint. xs and ys are each a list of IntVar. n is an IntVar"
  [xs ys n]
  (AmongVar. (into-array xs) (into-array ys) n))

;; (defn and
;;   "And among all the constraints"
;;   [constraints]
;;   (And. (into-array constraints)))

(defn and-bool
  [vars result]
  (AndBool. (into-array vars) result))

(defn circuit
  [vars]
  (Circuit. (into-array vars)))
  

(defn neq 
  "Creates and returns either an XneqC or XneqY instance.
   XneqC is returned if y is a Number."
  [x y]  
  (if (instance? Number y)
    (XneqC. x y)
    (XneqY. x y)))

(defn eq
  "Creates and returns either an XeqC or XeqY instance.
   XeqC is returned if y is a Number."
  [x y]
  (if (instance? Number y)
    (XeqC. x y)
    (XeqY. x y)))

(defn x+c=z
  "x and z are IntVar and c is an int"
  [x c z]
  (XplusCeqZ. x c z))

(defn notc
  [var]
  (Not. var))