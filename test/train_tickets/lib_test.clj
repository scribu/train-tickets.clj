(ns train-tickets.lib-test
  (:require [clojure.test :refer :all]
            [train-tickets.lib :refer :all]))

(deftest seat-purchasing
  (testing "can't buy more seats than the coach has"
    (is (= nil (-> (empty-coach 3 2)
                   (find-seats 7)))))

  (testing "can fill more than the first compartment"
    (is (= (set (range 5)) (-> (empty-coach 3 4)
                               (find-seats 5)))))

  (testing "groups seats in a single compartment"
    (is (= #{4 5 6 7} (-> (empty-coach 3 4)
                          (occupy-seats #{0 1 2})
                          (find-seats 4)))))

  (testing "groups seats in as few compartments as possible"
    (is (= #{3 8 9 10 11} (-> (empty-coach 3 4)
                          (occupy-seats #{0 1 2})
                          (occupy-seats #{4 5 6})
                          (find-seats 5))))))
