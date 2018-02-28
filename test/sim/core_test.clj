(ns sim.core-test
  (:require [clojure.test :refer :all]
            [sim.core :refer :all]))

(deftest def-image-test
  (testing "Testing existence of @image atom."
    (swap! image :assoc [])
    (is (= [] @image))))

(deftest print-image-test
  (testing "Testing (S) print function used to print @image atom."
    (is (= @image (sim.core/S)))))

(deftest valid-dimension-input-test
  (testing "Testing all input dimensions are valid."
    (is (true? (sim.core/validate-dimension-input [1 100 200 250])))))

(deftest lower-range-invalid-dimension-input-test
  (testing "Testing one or more invalid input dimensions below a value of 1."
    (is (false? (sim.core/validate-dimension-input [0 250])))))

(deftest upper-range-invalid-dimension-input-test
  (testing "Testing one or more invalid input dimensions above a value 250."
    (is (false? (sim.core/validate-dimension-input [1 251])))))

(deftest update-image-test
  (testing "Testing the update-image function."
    (is (= [["O" "O" "O"]["O" "O" "O"]] (sim.core/update-image (into [] (repeat 2 (into [] (take 3 (repeat "O"))))))))))

(deftest new-image-test
  (testing "Testing the creation of a new image."
    (is (= (sim.core/I 5 6) [["O" "O" "O" "O" "O"]
                             ["O" "O" "O" "O" "O"]
                             ["O" "O" "O" "O" "O"]
                             ["O" "O" "O" "O" "O"]
                             ["O" "O" "O" "O" "O"]
                             ["O" "O" "O" "O" "O"]]))))

(deftest L-update-test
  (testing "Testing the L function for update a single point in the image."
    (sim.core/I 5 6)
    (is (= (sim.core/L 2 2 "X") [["O" "O" "O" "O" "O"]
                                 ["O" "X" "O" "O" "O"]
                                 ["O" "O" "O" "O" "O"]
                                 ["O" "O" "O" "O" "O"]
                                 ["O" "O" "O" "O" "O"]
                                 ["O" "O" "O" "O" "O"]]))))

(deftest V-update-invalid-input-test
  (testing "Testing the L function for update a single point in the image."
    (sim.core/I 5 6)
    (is (= (sim.core/V 3 4 2 "W")
           (str "The values you supplied won't work. Please check that 1 <= X <= "
                (count (first @image))
                " and that 1 <= Y1 <= Y2 <= "
                (count @image) ".")))))

(deftest V-update-test
  (testing "Testing the L function for update a single point in the image."
    (sim.core/I 5 6)
    (is (= (sim.core/V 3 2 4 "W") [["O" "O" "O" "O" "O"]
                                 ["O" "O" "W" "O" "O"]
                                 ["O" "O" "W" "O" "O"]
                                 ["O" "O" "W" "O" "O"]
                                 ["O" "O" "O" "O" "O"]
                                 ["O" "O" "O" "O" "O"]]))))

(deftest H-update-invalid-input-test
  (testing "Testing the L function for update a single point in the image."
    (sim.core/I 5 6)
    (is (= (sim.core/H 3 2 6 "W")
           (str "The values you supplied won't work. Please check that 1 <= X1 <= X2 <= "
                (count (first @image))
                " and that 1 <= Y <= "
                (count @image) ".")))))

(deftest H-update-test
  (testing "Testing the L function for update a single point in the image."
    (sim.core/I 5 6)
    (is (= (sim.core/H 2 4 3 "W") [["O" "O" "O" "O" "O"]
                                 ["O" "O" "O" "O" "O"]
                                 ["O" "W" "W" "W" "O"]
                                 ["O" "O" "O" "O" "O"]
                                 ["O" "O" "O" "O" "O"]
                                 ["O" "O" "O" "O" "O"]]))))

(deftest get-point-value-test
  (testing "Testing the L function for update a single point in the image."
    (sim.core/I 3 3)
    (sim.core/L 2 2 "X")
    (is (= (sim.core/get-point-value 2 2) "X"))))
