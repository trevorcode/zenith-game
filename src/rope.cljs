(ns rope
  (:require-macros [macros :refer [def-method]])
  (:require [engine.animation :refer [draw-image draw-image-physics]]
            [engine.assets :as assets]
            [gamestate :refer [render-entity update-entity]]
            [transform :as transform]
            ["matter-js" :as matter]))

(def-method update-entity :rope [{:keys [transform]} dt])

(def-method render-entity :rope
  [this ctx]
  (let [image (get-in assets/images [:ship :image])
        ropeBodies (get-in this [:body :bodies])]
    (doseq [rope ropeBodies]
      (draw-image-physics ctx image {:scale 1 :body rope}))))

(defn create [{:keys [x y target]}]
  (let [group (matter/Body.nextGroup true)
        rope (matter/Composites.stack 100 50 1 8 10 10
                                      (fn [x y]
                                        (matter/Bodies.rectangle
                                         x y 30 5 {:collisionFilter {:group group}})))
        constraintA (matter/Constraint.create {:bodyB (first rope.bodies)
                                               :pointB {:x -25 :y 0}
                                               :pointA {:x (-> rope :bodies first :position :x)
                                                        :y (-> rope :bodies first :position :y)}
                                               :stiffness 0.5})]
    (matter/Composites.chain rope 0.5 0 -0.5 0 {:stiffness 0.8
                                                :length 2})
    (matter/Composite.add rope constraintA)
    (-> {:type :rope
         :body rope })))

