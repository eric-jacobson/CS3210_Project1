#lang racket
(define(genTours n)
  null)

(define(score index tour)
  null)

(define(etsp points)
  null)

(define(buildList n)
  (if (= n 0)
      null
      (cons n (buildList (- n 1)))))
