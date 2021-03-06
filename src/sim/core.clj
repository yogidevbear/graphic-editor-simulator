(ns sim.core
  (:require [clojure.spec.alpha :as s]
            [com.rpl.specter :as sp]
            [clojure.pprint :as pp])
  (:gen-class))

(def image
  "I define an image atom initialised as an empty vector"
  (atom []))
(S)
(defn S
  "I define a function that prints the current image atom"
  []
  @image)

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
  (swap! image :assoc f));use of reset!

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
    (str "In the function (I M N), both M and N need to be integer values between 1 and 250. You supplied "
         M " and " N ". Please try again.")))

(defn C
  "I define a function that swap!s all values in current image atom with \"0\""
  []
  (I (count (first @image)) (count @image)))

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

(defn F
  "I define a function that fills the region R with the colour C.
  R is defined as: Pixel (X,Y) belongs to R. Any other pixel which
  is the same colour as (X,Y) and shares a common side with any
  pixel in R also belongs to this region."
  [X Y C]
  (if (and (<= 1 X (count (first @image)))
           (<= 1 Y (count @image)))
    (let [_C (get-point-value X Y)]
      (L X Y C)
      (map
        #(if (= _C (get-point-value (inc (first %)) (inc (last %))))
          (F (inc (first %)) (inc (last %)) C))
        (get-adjacent-points [X Y])))
    (str "The values you supplied won't work. Please check that 1 <= X <= "
         (count (first @image))
         " and that 1 <= Y <= "
         (count @image) ".")))

(defn get-n-length-boundary-points
 "I define a function that works out the value surrounding points that form
 a square n pixels away from a given point."
 [coll i]
 (let [x (first coll)
       y (last coll)
       bpoints {:nw [(- x i) (- y i)]
        :ne [(+ x i) (- y i)]
        :se [(+ x i) (+ y i)]
       :sw [(- x i) (+ y i)]}]
    bpoints))

(defn R
  "I define a function that fills in incremental sqares around a single point"
  [X Y coll]
  (map-indexed
    (fn [idx itm]
      (let [bpoints (get-n-length-boundary-points [X Y] (inc idx))]
        (H (first (:nw bpoints))
           (first (:ne bpoints))
           (last (:nw bpoints))
           itm)
        (H (first (:sw bpoints))
           (first (:se bpoints))
           (last (:sw bpoints))
           itm)
        ))
        ;[X1 X2 Y C]
        ;(V [(get bpoints : Y1 Y2 C])))
    coll))
(S)
(defn -main [& _]
  (loop []
    (let [_ (do (print "=> ") (flush))
          args (clojure.string/split (read-line) #" ")]
      (when-not (= "X" (first args))
        (if-let [f (resolve (symbol "sim.core" (first args)))]
          (let [result (apply f (map #(try (Long/parseLong %) (catch Exception _ %)) (rest args)))]
            (when (= "S" (first args))
              (pp/pprint result)))
          (println "Sorry, I don't understand the" (first args) "command!"))
        (recur)))))
