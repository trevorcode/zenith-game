(ns rope
  (:require-macros [macros :refer [def-method]])
  (:require [engine.animation :refer [draw-image draw-image-physics] :as animation]
            [engine.assets :as assets]
            [gamestate :refer [render-entity update-entity]]
            [transform :as transform]
            ["matter-js" :as matter]))
(assets/register-animations
 {:rope {:sheet :runesheet
         :height 32
         :width 32
         :duration 10
         :durationCounter 0
         :frame 0
         :rows 3
         :columns 2
         :cells [4]
         :loop false}})
(def-method update-entity :rope [{:keys [transform]} dt])

(def-method render-entity :rope [{:keys [animation]
                                  :as this} ctx]
  (let [ropeBodies (get-in this [:bodies :bodies])]
    (doseq [rope ropeBodies]
      (animation/draw-animation-physics {:body rope :scale 1.25} animation ctx))))

(defn destroy-rope [scene {:keys [id bodies] :as rope}]
  (matter/Composite.remove scene.physics.world bodies true)
  (set! scene.objects
        (filterv #(not= (:id %) id) scene.objects)))

(defn create [{:keys [x y target]}]
  (let [group (matter/Body.nextGroup true)
        randomLength (+ (* (js/Math.random) 7) 1)
        ropeBodies (matter/Composites.stack x y 1 randomLength 10 10
                                            (fn [x1 y1]
                                              (matter/Bodies.rectangle
                                               x1 y1 5 30 {:isSensor true})))
        constraintA (matter/Constraint.create {:bodyB (first ropeBodies.bodies)
                                               :pointB {:x 0 :y -25}
                                               :pointA {:x (-> ropeBodies :bodies first :position :x)
                                                        :y (-> ropeBodies :bodies first :position :y)}
                                               :stiffness 0.5
                                               :length 1})

        constraintB (matter/Constraint.create {:bodyA (last ropeBodies.bodies)
                                               :pointA {:x 0 :y 25}
                                               :bodyB (:body target)
                                               :stiffness 0.5
                                               :length 3})
        rope (-> {:type :rope
                  :id (* 200000 (js/Math.random))
                  :bodies ropeBodies
                  :renderIndex 4
                  :scale 2}
                 (animation/play-animation :rope))]
    (matter/Composites.chain ropeBodies 0 0.5 0 -0.5 {:stiffness 0.8
                                                      :length 2})
    (matter/Composite.add ropeBodies [constraintA constraintB])
    (conj! target.ropes rope)
    rope))

