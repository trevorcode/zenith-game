(ns rope
  (:require-macros [macros :refer [def-method]])
  (:require [engine.animation :as animation]
            [engine.assets :as assets]
            [gamestate :refer [render-entity update-entity]]
            [util :as util]
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

(def-method update-entity :rope [{:keys [successfulComboTimer] :as rope} dt]
  (when successfulComboTimer
    (set! rope.successfulComboTimer (dec successfulComboTimer))))

(def-method render-entity :rope [{:keys [animation successfulComboTimer]
                                  :as this} ctx]
  (let [ropeBodies (get-in this [:bodies :bodies])
        ropeFilter (if successfulComboTimer
                     (str
                      "brightness("
                      (+ 1.5 (js/Math.pow (- 100 this.successfulComboTimer) 0.25))
                      ") opacity(" this.successfulComboTimer "%)")
                     nil)]
    (doseq [rope ropeBodies]
      (animation/draw-image-cell ctx (get-in assets/images [:runesheet :image])
                                 {:x (-> rope :position :x)
                                  :y (-> rope :position :y)
                                  :scale 1.25
                                  :rotation (-> rope :angle)
                                  :width 32
                                  :height 32
                                  :columns 2
                                  :cell 4
                                  :filter ropeFilter}))))

(defn destroy-rope [scene {:keys [id bodies] :as rope}]
  (matter/Composite.remove scene.physics.world bodies true)
  (set! scene.objects
        (filterv #(not= (:id %) id) scene.objects)))

(defn create [{:keys [x y target]}]
  (let [dynamicLength (+ (int
                          (/ (util/distance x y target.body.position.x target.body.position.y) 50))
                         1)
        ropeBodies (matter/Composites.stack x y 1 dynamicLength 10 10
                                            (fn [x1 y1]
                                              (matter/Bodies.rectangle
                                               x1 y1 5 30 {:collisionFilter {:mask 0}})))
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
        rope {:type :rope
              :id (* 200000 (js/Math.random))
              :bodies ropeBodies
              :renderIndex 4
              :scale 2}]
    (matter/Composites.chain ropeBodies 0 0.5 0 -0.5 {:stiffness 0.8
                                                      :length 2})
    (matter/Composite.add ropeBodies [constraintA constraintB])
    (conj! target.ropes rope)
    rope))

