(ns otm-technical-test.core
  (:gen-class))

(def image
  "I define an image atom initialised as an empty vector"
  (atom [1 2 3]))

(defn X
  "I define a function that swap!s image atom to the initial empty vector state"
  []
  (swap! image :assoc []))

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
