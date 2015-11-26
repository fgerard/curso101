(ns clojure101.core
  (:require
   [clojure.pprint :as pp]
   [clojure.core.match :as M]))

;; (defn foo
;;   "I don't do a whole lot."
;;   [x]
;;   (str "Hello, " x "!"))

;; (foo "felipe")

;; (defn cuadrado [n]
;;   (println "calculando cuadrado de " n)
;;   (* n n))

;; (cuadrado 4)

;; (def cuads (map cuadrado (range 1 1000)))

;; (take 10 cuads)


;; (defn fact [n]
;;   (reduce * 1N (range 2 (inc n))))

;; (def  facts (pmap fact (range 1 5000)))

;; (last facts)

;; (defn qsort [[p & resto :as data]]
;;     (if (seq data)
;;        (lazy-cat (qsort (filter #(< % p) resto))
;;                      (list p)
;;                      (qsort (filter #(>= % p) resto)))))

;; (def v (vec (repeatedly 100000 #(rand-int 10000))))

;; (count v)
;; (reduce + v)
;; (def X (qsort v))
;; (reduce + X)
;; (take 10 X)
;; (last X)

;; (last X)

;; (for [x (range 0 3) y (range 0 3)]
;;   [x y])

;; (for [x (range 0 3) y (range 0 3) :when (not= x y)]
;;   [x y])

;; (take 10 (for [x (range 0 300)
;;                      y (range 0 300)
;;                      :when (not= x y)]
;;                 [x y]))


;; (def fib (cons 1N (cons 1N (lazy-seq (map + fib (rest  fib))))))

; [1 1 2 3 5 8]
; [1 2 3 5 8]
;  2 3 5 8 13


;; (take 60 fib)

;; (def fib2 (lazy-cat '(1N 1N) (map + fib2 (rest fib2))))

;; (get  (vec (take 10000 fib2)) 8)

;; (take 10 fib2)

;; (defn cuadradosF [xs]
;;   (map #(* % %) xs))
;; (cuadradosF [1 2 3 4 6])

;; (defmacro cuadradosM [xs]
;;   `(map #(* % %) ~xs))
;; (cuadradosM [1 2 3 4 5])
;; (macroexpand-1 '(cuadradosM [1 2 3 4 6]))

;; (doseq [n (range 1 101)]
;;   (M/match
;;      [(mod n 3) (mod n 5)]
;;     [0 0] "FizzBuzz"
;;     [0 _] "Fizz"
;;     [_ 0] "Buzz"
;;     :else n))

;; (map (fn [n]
;;            (M/match [(mod n 3) (mod n 5)]
;;              [0 0] "FizzBuzz"
;;              [0 _] "Fizz"
;;              [_ 0] "Buzz"
;;              :else n)) (range 1 101))

;; (use '(incanter core charts) :reload-all)

;; (view (function-plot #(* % % (sin %)) -20 20))

;; (view (function-plot #(* % (sin %)) -20 20))








