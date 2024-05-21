(ns nadir
  (:require-macros [macros :refer [def-method]])
  (:require [engine.animation :refer [draw-image draw-image-physics]]
            [engine.assets :as assets]
            [gamestate :refer [render-entity update-entity]]
            [transform :as transform]
            ["matter-js" :as matter]
            [engine.input :as input]))


(def group (matter/Body.nextGroup true))
(def platform (matter/Bodies.rectangle 100 400 200 25 {:collisionFilter {:group group}
                                                       :frictionStatic 100}))
#_(def base (matter/Bodies.rectangle 300 500 30 150 {:isStatic true :collisionFilter {:group group}}))
(def base
  (matter/Body.create
   {:parts [(matter/Bodies.rectangle 100 500 200 30)
            (matter/Bodies.circle 100 500 25 {:isStatic true :collisionFilter {:group group}})]

    :isStatic true }))
(def constraint1(matter/Constraint.create
                 {:bodyA platform
                  :pointA {:x -80 :y 0}
                  :bodyB base
                  :pointB {:x -80 :y 0}
                  :stiffness 0.03
                  :length 100
                  }))

(def constraint2 (matter/Constraint.create
                 {:bodyA platform
                  :pointA {:x 80 :y 0}
                  :bodyB base
                  :pointB {:x 80 :y 0}
                  :stiffness 0.03
                  :length 100
                  }))

(def constraint3 (matter/Constraint.create
                 {:bodyA platform
                  :pointA {:x -80 :y 0}
                  :bodyB base
                  :pointB {:x 0 :y 0}
                  :stiffness 0.03
                  :length 150
                  }))

(def constraint4 (matter/Constraint.create
                 {:bodyA platform
                  :pointA {:x 80 :y 0}
                  :bodyB base
                  :pointB {:x 0 :y 0}
                  :stiffness 0.03
                  :length 150
                  }))

(def-method update-entity :nadir [{{base :base} :bodies} dt]
  (when (input/key-down? (get input/keys :A))
    (matter/Body.setPosition base {:x (- base.position.x 1) :y base.position.y}))
  (when (input/key-down? (get input/keys :D))
    (matter/Body.setPosition base {:x (+ base.position.x 1) :y base.position.y})))

(def-method render-entity :nadir
  [ship ctx]
  (let [image (get-in assets/images [:ship :image])]
    (draw-image-physics ctx image ship)))

(defn create []
  (-> {:type :nadir
       :bodies {:platform platform
                :base base
                :constraint1 constraint1
                :constraint2 constraint2
                :constraint3 constraint3
                :constraint4 constraint4
                
                }}))

