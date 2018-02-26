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
  "I define a function that works out the value vertical and horizontal adjacent points."
  [X Y]
  (let [up [(- X 1) (- Y 2)]
        right [X (- Y 1)]
        down [(- X 1) Y]
        left [(- X 2) (- Y 1)]]
    (into #{} (filter some?
                (map #(if (and (<= 0 (first %) (dec (count (first @image)))) ;Check that the x coordinate is within the bounds of @image
                               (<= 0 (first (rest %)) (dec (count @image)))) ;Check that the y coordinate is within the bounds of @image
                        %) [up right down left])))))

(S)
(build-matrix 1 1 "O" "_" #{} #{})
(defn build-matrix
  [X Y C1 C2 tested-matrix final-matrix]
  (if (= (get-point-value (dec X) (dec Y)) C1)
    (
     ;(L X Y C2)
     (map #(build-matrix (first %) (last %) C1 C2 tested-matrix final-matrix) (get-adjacent-points X Y)))
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
(defn wtf
  [X Y C1 C2 matrix]
  (let [up [(- X 1) (- Y 2)]
        right [X (- Y 1)]
        down [(- X 1) Y]
        left [(- X 2) (- Y 1)]]
    (clojure.set/union
      matrix
      (into #{}
        (filter some? (map #(if (and
                    (<= 0 (first %) (count (first @image))) ;Check that the x coordinate is within the bounds of @image
                    (<= 0 (first (rest %)) (count @image)) ;Check that the y coordinate is within the bounds of @image
                    (= (get-point-value (inc (first %)) (inc (first (rest %)))) C1) ;Check that colour at coordinate matches the originating point colour in F function call
                    )
                %) [up right down left]))))))
;    (if (and (<= 1 (first up) (count (first @image))) (<= (first (rest up)) (count @image)))
;      (conj matrix up))))
    ;(conj matrix up right down left)))
(wtf 1 1 "O" "X" #{})
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

(I 10 10)
(L 3 3 "W")
(H 2 6 5 "X")
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
