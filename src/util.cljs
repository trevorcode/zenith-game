(ns util)

(defn distance [x1 y1 x2 y2]
  (js/Math.sqrt (+ (js/Math.pow (- x2 x1) 2)
                   (js/Math.pow (- y2 y1) 2))))


(defn random-between [min max]
  (+ min (* (js/Math.random) (- max min))))

(defn random-between-int [min max]
  (js/Math.floor (+ min (* (js/Math.random) (- max min)))))
