(ns train-tickets.lib
  (:require [clojure.string :refer (join)]
            [clojure.set :refer (union)])
  (:gen-class))

(defn empty-coach
  "Generate an empty coach"
  [num-comp comp-size]
  (hash-map :num-comp num-comp :comp-size comp-size :occupied #{}))

(defn occupy-seats
  "Given a list of seats to occupy, return an updated coach"
  [coach seats]
  (update-in coach [:occupied] union seats))

(defn empty-seats
  "Get a list of non-occupied seats"
  [{:keys [num-comp comp-size occupied]}]
  (filter
    #(not (contains? occupied %))
    (range (* num-comp comp-size))))

(defn empty-seats-in-comp
  "Get a list of non-occupied seats in a compartment"
  [{:keys [num-comp comp-size occupied]} comp-nr]
  (let [seat-start (* comp-nr comp-size)]
    (if (>= comp-nr num-comp) (throw (Exception. "Invalid compartment number")))
    (filter #(not (contains? occupied %))
        (range seat-start (+ seat-start comp-size)))))

(defn find-seats-contiguous
  "Find a given number of empty seats"
  [coach how-many]
  (loop [i 0 found-seats #{}]
    (if (or (>= i (:num-comp coach))
            (== (count found-seats) how-many))
        found-seats
        (let [empty-seats (empty-seats-in-comp coach i)
              need-to-find (min (:comp-size coach) (- how-many (count found-seats)))]
          (if (>= (count empty-seats) need-to-find)
            (recur (inc i) (union found-seats (take need-to-find empty-seats)))
            (recur (inc i) found-seats))))))

(defn find-seats-remainder
  "Find available seats after contiguous blocks have been found"
  [coach found-seats how-many]
  (union found-seats
         (take (- how-many (count found-seats))
               (filter #(not (contains? found-seats %))
                       (empty-seats coach)))))

(defn find-seats
  "Find available seats"
  [coach how-many]
  (let [found-seats (find-seats-remainder coach
                                          (set (find-seats-contiguous coach how-many))
                                          how-many)]
    (if (== (count found-seats) how-many)
      found-seats)))

(defn -main
  [& args]
  (loop [coach (empty-coach 5 5)]
    (printf "Empty seats: %s\n" (join " " (empty-seats coach)))
    (print "Number of seats to buy: ")
    (flush)
    (let [input (read-string (read-line))]
      (if (not (number? input))
        (do
          (println "Please input a number!")
          (recur coach))
        (let [found-seats (find-seats coach input)]
          (if (nil? found-seats)
            (do
              (println "Not enough seats available")
              (recur coach))
            (do
              (printf "Found seats: %s\n" (join " " found-seats))
              (recur (occupy-seats coach found-seats)))))))))
