(ns
  ^{:author mate.magyari}
  clojure-study.euler)

(defn dividers [x nums]
  (filter #(zero? (rem x %)) nums))

(defn find-primes-under [n]
  (loop [primes [] x 2]
    (cond
      (> x n)
      primes
      (zero? (count (dividers x primes)))
      (recur (conj primes x) (inc x))
      :else (recur primes (inc x)))))

(defn power-triples [n]
  (let [pow-n (fn [exp] (Math/pow n exp)) ; n on the power of exp
        ps-2 (find-primes-under (pow-n 1/2)) ;primes under square root n
        ps-3 (filter #(<= % (pow-n 1/3)) ps-2) ;primes under cubic root n
        ps-4 (filter #(<= % (pow-n 1/4)) ps-3)] ;primes under forth root n
    (for [p2 ps-2 p3 ps-3 p4 ps-4
          :let [power-sum (+ (Math/pow p2 2)
                            (Math/pow p3 3)
                            (Math/pow p4 4))]
          :when (<= power-sum n)]
      [p2 p3 p4])))

(def n 50000000)
(def result (power-triples n))
(println (count result))

