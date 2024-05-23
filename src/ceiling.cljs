(ns ceiling
  (:require-macros [macros :refer [def-method]])
  (:require [engine.animation :refer [draw-image draw-image-physics]]
            [engine.assets :as assets]
            [gamestate :refer [render-entity update-entity]]
            ["matter-js" :as matter]))

(def entity :ceiling)

(def-method update-entity entity [{:keys [transform]} dt])

(def-method render-entity entity
  [ship ctx]
  (let [image (get-in assets/images [:ship :image])]
    (draw-image-physics ctx image ship)))

(defn create []
  (let [body (matter/Bodies.rectangle 400 0 800 150 {:isStatic true})]
    (-> {:type entity
         :body body
         :scale 5})))

