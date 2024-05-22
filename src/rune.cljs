(ns rune
  (:require-macros [macros :refer [def-method]])
  (:require [engine.animation :refer [draw-image draw-image-physics]]
            [engine.assets :as assets]
            [gamestate :refer [render-entity update-entity]]
            [transform :as transform]
            ["matter-js" :as matter]))

(def-method update-entity :rune [{:keys [transform]} dt])

(def-method render-entity :rune
  [ship ctx]
  (let [image (get-in assets/images [:ship :image])]
    (draw-image-physics ctx image ship)))

(defn create [{:keys [x y rotation]}]
  (let [body (matter/Bodies.rectangle x y 80 80)]
    (-> {:type :rune
         :body body
         :scale 5})))

