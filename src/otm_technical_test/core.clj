(ns otm-technical-test.core
  (:require [clojure.spec.alpha :as s]
            [com.rpl.specter :as sp])
  (:gen-class))

(def image
  "I define an image atom initialised as an empty vector"
  (atom []))

(defn X
  "I define a function that swap!s image atom to the initial empty vector state"
  []
  (swap! image :assoc []))

(defn S
  "I define a function that prints the current image atom"
  []
  (print @image))

(s/fdef I
  :args (s/cat :M #(s/int-in-range? 1 251 %)
               :N #(s/int-in-range? 1 251 %)))
(defn I
  "I define a function that takes a width (M) and height (N) as arguments
  and creates a 2 dimensional vector to represent an M by N pixel image"
  [M N]
  (if (validate-dimension-input [M N])
    (update-image (into [] (repeat N (into [] (take M (repeat "O"))))))
    (str "In the function (I M N), both M and N need to be integer values between 1 and 250. Please try again.")))

(defn L
  "I define a function that updates one cell within the 2D image vector
  with a new colour"
  [X Y C]
  (if (and (<= 1 X (count (first @image)))
           (<= 1 Y (count @image)))
    (update-image (assoc-in @image [(dec Y) (dec X)] C))
    (str "The values you supplied won't work. Please check that 1 <= X <= "
         (count (first @image))
         " and that 1 <= Y1 <= Y2 <= "
         (count @image) ".")))

(defn V
  "I define a function that updates a vertical segment within the 2D
  image vector with a new colour"
  [X Y1 Y2 C]
  (if (and
        (<= 1 X (count (first @image)))
        (<= 1 Y1 Y2 (count @image)))
    (update-image (sp/setval (mat-elems Y1 X Y2 X) C @image))
    (str "The values you supplied won't work. Please check that 1 <= X <= "
         (count (first @image))
         " and that 1 <= Y1 <= Y2 <= "
         (count @image) ".")))

(defn H
  "I define a function that updates a horizontal segment within the 2D
  image horizontal with a new colour"
  [X1 X2 Y C]
  (if (and
        (<= 1 X1 X2 (count (first @image)))
        (<= 1 Y (count @image)))
    (update-image (sp/setval (mat-elems Y X1 Y X2) C @image))
    (str "The values you supplied won't work. Please check that 1 <= X1 <= X2 <= "
         (count (first @image))
         " and that 1 <= Y <= "
         (count @image) ".")))

(defn mat-elems [row col end-row end-col]
  (let [row (dec row) col (dec col)]
    (sp/path (sp/srange row end-row) sp/ALL (sp/srange col end-col) sp/ALL)))

(defn update-image
  "I define a function that takes a function and swap!s
  the value of @image using the passed in function"
  [f]
  (swap! image :assoc f))

(defn validate-dimension-input
  "I define a function that takes a collection of values and
  check that each value conforms to the defined business rules
  for data type and min/max values"
  [coll]
  (not (some false? (map #(and (integer? %) (<= 1 % 250)) coll))))

(comment "
  Questions:
  ==========
  - How is the system going to be run/accessed?
    (e.g. make use of -main function or not, will commands come from REPL, etc.)
  - How are commands being read in by the system?
    (e.g. will commands be input as \"I 5 6\" or as \"(run I 5 6)\" or ???)
  - What happens when an invalid function letter is supplied?
    (i.e. anything other than I C L V H F S X)
  - What happens when anything other than a positive integer is supplied?
    (include logical types like negative integers, zero, nil, etc)
  - Other considerations...?
  ")
