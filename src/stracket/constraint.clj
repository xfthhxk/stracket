(ns stracket.constraint
  (:refer-clojure :rename {not= core-not=})
  (:import [JaCoP.constraints
            AbsXeqY Alldiff Alldifferent Alldistinct Among AmongVar And AndBool
            Circuit
            Not
            Sum SumWeight
            XeqC XeqY XmulCeqZ XneqC XneqY XplusCeqZ XplusYeqZ]))

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

(defn andc
  "And among all the constraints"
  [constraints]
  (And. (into-array constraints)))

(defn and-bool
  "If all vars are equal to 1 then result is 1 also. Otherwise, result is 0.
   Restricts domain of all vars as well as result to between 0 and 1."
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

(defn x+y=z
  "x + y = z, all IntVars"
  [x y z]
  (XplusYeqZ. x y z))

(defn x*c=z
  "x and z are IntVar and c is an int"
  [x c z]
  (XmulCeqZ. x c z))

(defn not=
  "invert the relationship"
  [var]
  (Not. var))

(defn sum=
  "Sums all vars to equal sum.
   vars is a seq of IntVar. sum is an IntVar."
  [vars sum]
  (Sum. (into-array vars) sum))

(defn sum-weight=
  "Weighted sum of all vars equals sum"
  [vars weights sum]
  (SumWeight. (into-array vars) (into-array Integer/TYPE (map int weights)) sum))

