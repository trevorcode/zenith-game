(ns scene
  (:require [background :as background]
            [gamestate :as gs]
            [engine.input :as input]
            [greencap :as greencap]
            [runguy :as runguy]
            [ship :as ship]
            [nadir :as nadir]
            ["matter-js" :as matter]))

(defn scene-draw [scene context]
  (doseq [game-obj (:objects scene)]
    (gs/render-entity game-obj context)))

(defn scene-update [scene dt]
  (doseq [game-obj (:objects scene)]
    (gs/update-entity game-obj dt)))




(defn scene1 []
  (let [scene {:objects
               (into [(background/create)
                      (ship/create-ship {:x 50 :y 50 :rotation 10})
                      (ship/create-ship {:x 30 :y 80 :rotation 90})
                      (runguy/create {:x 180 :y 180})
                      (nadir/create)]
                     (take 2 (repeatedly
                              #(greencap/create
                                {:x (* (js/Math.random) 350)
                                 :y (* (js/Math.random) 350)
                                 :rotation (* (js/Math.random) 350)}))))
               :physics (let [engine (matter/Engine.create)
                              ground (matter/Bodies.rectangle 400 610 810 90 {:isStatic true})
                              render (matter/Render.create {:element js/document.body
                                                            :engine engine})

                              mouse (matter/Mouse.create (:canvas gs/game-state))
                              mouseConstraint (matter/MouseConstraint.create engine
                                                                             {:mouse mouse
                                                                              :constraint {:stiffness 0}})]
                          (matter/Composite.add engine.world ground)
                          (matter/Composite.add engine.world mouseConstraint)
                          (matter/Render.run render)
                          (matter/Runner.run (matter/Runner.create) engine)
                          engine)}]
    (println (filterv #(contains? % :body) (:objects scene)))
    (matter/Composite.add scene.physics.world (mapv :body (filterv #(contains? % :body) (:objects scene))))
    (matter/Composite.add scene.physics.world (into [] (mapcat (comp vals :bodies) (filterv #(contains? % :bodies) (:objects scene)))))
    scene))
