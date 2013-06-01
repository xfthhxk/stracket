(ns stracket.etc)

(defn shape
  "Give shape to a seq specified by rows and cols"
  [xs rows cols]
  (partition-all cols xs))

(defn extract-row
  [shaped n]
  (nth shaped n))

(defn extract-col
  [shaped n]
  (let [rows-with-cols (filter #(> (count %) n) shaped)]
  (map #(nth % n) rows-with-cols)))

