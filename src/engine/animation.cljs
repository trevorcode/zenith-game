(ns engine.animation)

(defn get-cell-x-y [cell columns]
  [(int (/ cell columns)) (mod cell columns)])


(defn draw-image [ctx image {:keys [x y rotation scale]}]
  (ctx.setTransform scale 0 0 scale x y)
  (when rotation
    (ctx.rotate rotation))
  (ctx.drawImage image (/ (- image.width) 2) (/ (- image.height) 2))
  (ctx.setTransform 1 0 0 1 0 0))

(defn draw-image-cell [ctx image {:keys [x y rotation scale filter
                                         width height columns cell]}]

  (when (not= filter "none")
    (set! ctx.filter filter))
  (ctx.setTransform scale 0 0 scale x y)
  (when rotation
    (ctx.rotate rotation))
  (let [[frame-x frame-y] (get-cell-x-y (or cell 0) (or columns 1))]
    (ctx.drawImage image
                   (* frame-y width)
                   (* frame-x height)
                   width
                   height
                   (/ (- width) 2)
                   (/ (- height) 2)
                   width
                   height))
  (ctx.setTransform 1 0 0 1 0 0)
  (set! ctx.filter "none"))
