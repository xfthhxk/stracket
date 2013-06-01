(ns stracket.etc-test
  (:use midje.sweet)
  (:use stracket.etc))

(fact "test shape"
      (let [xs '[a b c d e f g h]]
        
        (shape xs 2 4) => '[[a b c d]
                            [e f g h]]
        
        (shape xs 4 2) => '[[a b]
                            [c d]
                            [e f]
                            [g h]]
        (shape xs 3 3) => '[[a b c]
                            [d e f]
                            [g h]]))

(fact "extract-row"
      (let [shaped (shape '[a b c d e f g h] 3 3)]
        (extract-row shaped 0) => '[a b c]
        (extract-row shaped 1) => '[d e f]
        (extract-row shaped 2) => '[g h]))

(fact "extract-col"
      (let [shaped (shape '[a b c d e f g h] 3 3)]
        (extract-col shaped 0) => '[a d g]
        (extract-col shaped 1) => '[b e h]
        (extract-col shaped 2) => '[c f]))




        







