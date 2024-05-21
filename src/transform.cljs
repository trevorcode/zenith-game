(ns transform)

(defn attach-transform
  [obj {:keys [x y scale rotation]}]
  (assoc obj :transform {:x (or x 0)
                         :y (or y 0)
                         :scale (or scale 1)
                         :rotation (or rotation 0)}))

