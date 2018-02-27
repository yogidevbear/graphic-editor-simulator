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

(defn validate-dimension-input
  "I define a function that takes a collection of values and
  check that each value conforms to the defined business rules
  for data type and min/max values"
  [coll]
  (not (some false? (map #(and (integer? %) (<= 1 % 250)) coll))))

(defn update-image
  "I define a function that takes a function and swap!s
  the value of @image using the passed in function"
  [f]
  (swap! image :assoc f))

(defn mat-elems [row col end-row end-col]
  (let [row (dec row) col (dec col)]
    (sp/path (sp/srange row end-row) sp/ALL (sp/srange col end-col) sp/ALL)))

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
         " and that 1 <= Y <= "
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

(defn get-point-value
  "I define a function that gets the currently set value for a particular point
  within the 2D image."
  [X Y]
  (nth (nth @image (dec Y)) (dec X)))

(defn get-adjacent-points
  "I define a function that works out the value vertical and
  horizontal adjacent points for a given set of coordinates."
  [coll]
  (let [x (first coll)
        y (last coll)
        up [(- x 1) (- y 2)]
        right [x (- y 1)]
        down [(- x 1) y]
        left [(- x 2) (- y 1)]]
        (filter some?
          (map #(if (and (<= 0 (first %) (dec (count (first @image))))
                         (<= 0 (last %) (dec (count @image))))
                  %) [up right down left]))))

(I 5 5)
(L 3 3 "W")
(H 2 4 2 "X")
(S)
(into #{} (apply concat (map #(get-adjacent-points %) #{[1 1]})))

(defn F
  "I define a function that fills the region R with the colour C.
  R is defined as: Pixel (X,Y) belongs to R. Any other pixel which
  is the same colour as (X,Y) and shares a common side with any
  pixel in R also belongs to this region."
  [X Y C]
  (let [C1 (get-point-value X Y)]
    (map (fn [xy] (let [x (first xy)
                        y (last xy)]
                    (if (= C1 (get-point-value x y))
                      ((L x y C)
                       (into #{} (apply concat (map #(get-adjacent-points %) #{[x y]}))))))))
      #{[X Y]}))
(I 5 5)
(L 3 3 "W")
(H 2 4 2 "X")
(S)
(F 1 1 "P")
(defn F
  [X Y C]
  (let [_C (get-point-value X Y)]
    (L X Y C)
    (map
      #(if (= _C (get-point-value (first %) (last %)))
        (recur [(inc (first %)) (inc (last %)) C]))
      (get-adjacent-points [X Y]))))
(get-point-value (first [1 1]) (last [1 1]))
(get-adjacent-points [1 1])

(build-matrix 1 1 "O" "_" #{} #{})
(defn build-matrix
  [X Y C1 C2 tested-matrix final-matrix]
  (if (= (get-point-value X Y) C1)
    ;(
     ;(L X Y C2)
     (map (recur (inc (first %)) (inc (last %)) C1 C2 (clojure.set/union tested-matrix %) (clojure.set/union final-matrix %)) (get-adjacent-points X Y)) ;)
    final-matrix))
  (loop []
    (when (> (count adj-points) 1)
      matrix
      (if (not (contains? matrix (first adj-points)))
        (clojure.set/union matrix (first adj-points))
        ))))
  (let [adj-points (get-adjacent-points X Y)]
    (clojure.set/union matrix adj-points)
    ()))
(let [ap (get-adjacent-points 1 1)]
  (loop ap))
(defn F
  "I define a function that fills the region R with the colour C.
  R is defined as: Pixel (X,Y) belongs to R. Any other pixel which
  is the same colour as (X,Y) and shares a common side with any
  pixel in R also belongs to this region."
  [X Y C]
  (if (and (<= 1 X (count (first @image)))
           (<= 1 Y (count @image)))
    (let [C1 (get-point-value X Y)
          C2 C
          xmax (count (first @image))
          ymax (count @image)]
      (fn [X Y C1 C2 matrix]
        ;([X Y C1 C2 matrix]))
        ())
      [X Y C1 C2 #{}])
    (str "The values you supplied won't work. Please check that 1 <= X <= "
         (count (first @image))
         " and that 1 <= Y <= "
         (count @image) ".")))

(I 5 5)
(L 3 3 "W")
(H 2 4 2 "X")
(V 5 3 8 "I")
(S)
(F 4 6 "@")

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
