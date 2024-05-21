(ns ship
  (:require-macros [macros :refer [def-method]])
  (:require [engine.animation :refer [draw-image draw-image-physics]]
            [engine.assets :as assets]
            [gamestate :refer [render-entity update-entity]]
            [transform :as transform]
            ["matter-js" :as matter]))

(def-method update-entity :ship [{:keys [transform]} dt]
  (assoc! transform :rotation (+ 0.02 (:rotation transform)))
  (assoc! transform :x (+ 0.21 (:x transform))))

(def-method render-entity :ship
  [ship ctx]
  (let [image (get-in assets/images [:ship :image])]
    (draw-image-physics ctx image ship)))

(defn create-ship [{:keys [x y rotation]}]
  (-> {:type :ship
       :body (matter/Bodies.rectangle x y 80 80)}
      (transform/attach-transform {:x x
                                   :y y
                                   :rotation rotation
                                   :scale 5})))

