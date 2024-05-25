(ns ceiling
  (:require-macros [macros :refer [def-method]])
  (:require [engine.assets :as assets]
            [gamestate :refer [render-entity update-entity]]
            ["matter-js" :as matter]))

(def entity :ceiling)

(def-method update-entity entity [{:keys [transform]} dt])

(def-method render-entity entity
  [ceiling ctx]
  (let [image (get-in assets/images [:banner :image])]
    (ctx.setTransform 10 0 0 10 400 75)
    (set! ctx.filter "brightness(1.7) saturate(50%) hue-rotate(150deg) contrast(150%)
                      drop-shadow(0px 0px 20px black) opacity(70%)")
    (ctx.drawImage image (/ (- image.width) 2) (/ (- image.height) 2))
    (ctx.setTransform 1 0 0 1 0 0)
    (set! ctx.filter "none")))


(defn create []
  (let [body (matter/Bodies.rectangle 400 75 800 150 {:isStatic true :isSensor true})]
    (-> {:type entity
         :body body
         :scale 5})))

