(ns scene
  (:require 
            [gamestate :as gs]
            [engine.input :as input]
            [greencap :as greencap]
            [runguy :as runguy]
            [rune :as rune]
            
            [ceiling :as ceiling]
            [rope :as rope]
            ["matter-js" :as matter]))

(defn scene-draw [scene context]
  (doseq [ropes (filterv #(= :rope (:type %)) (:objects scene))]
    (gs/render-entity ropes context))
  (doseq [game-obj (filterv #(not= :rope (:type %)) (:objects scene))]
    (gs/render-entity game-obj context))

  (let [mousePosition (-> scene :mouse :mouse :position)
        selectedRunePos (-> scene :selectedRune :body :position)]
    (when selectedRunePos
      (context.beginPath)
      (context.moveTo (:x selectedRunePos) (:y selectedRunePos))
      (context.lineTo (:x mousePosition) (:y mousePosition))
      (context.stroke))))

(defn scene-update [scene dt]
  (doseq [game-obj (:objects scene)]
    (gs/update-entity game-obj dt)))

(defn register-obj [scene obj]
  (conj! scene.objects obj)
  (when (:body obj)
    (matter/Composite.add scene.physics.world (:body obj))))


(defn mouseDown [scene world ev]
  (let [runes (filterv #(and (contains? % :body)
                             (= "rune" (get % :type))) scene.objects)
        runeBodies (mapv #(get % :body) runes)
        selectedRune (first (filterv #(= (get % :body)
                                         (first (matter/Query.point runeBodies ev.mouse.position))) runes))]
    (set! scene.selectedRune selectedRune)
    (println (matter/Query.point runeBodies ev.mouse.position))))

(defn mouseUp [scene world ev]
  (let [ceiling (filterv #(= :ceiling (:type %)) scene.objects)
        mouseUpOnCeiling? (matter/Query.point (mapv :body ceiling) ev.mouse.position)]
    (when (not-empty mouseUpOnCeiling?)
      (register-obj scene (rope/create {:x ev.mouse.position.x
                                        :y ev.mouse.position.y
                                        :target scene.selectedRune}))))
  (set! scene.selectedRune nil))

(defn scene1 []

  (let [engine (let [engine (matter/Engine.create)
                     ground (matter/Bodies.rectangle 400 610 810 90 {:isStatic true})
                     render (matter/Render.create {:element js/document.body
                                                   :engine engine})]
                 (matter/Composite.add engine.world ground)

                 (matter/Render.run render)
                 (matter/Runner.run (matter/Runner.create) engine)
                 engine)
        objects (into [
                       (rune/create {:x 300 :y 50 :rotation 10})
                       (rune/create {:x 400 :y 80 :rotation 90})
                       (runguy/create {:x 180 :y 180})
                       (ceiling/create)]
                      (take 2 (repeatedly
                               #(greencap/create
                                 {:x (* (js/Math.random) 350)
                                  :y (* (js/Math.random) 350)
                                  :rotation (* (js/Math.random) 350)}))))
        mouse (matter/Mouse.create (:canvas gs/game-state))
        mouseConstraint (matter/MouseConstraint.create engine
                                                       {:mouse mouse
                                                        :constraint {:stiffness 0}})
        scene {:type :scene
               :objects []
               :mouse mouseConstraint
               :physics engine}]

    (matter/Composite.add engine.world mouseConstraint)
    (matter/Events.on mouseConstraint "mousedown" (partial mouseDown scene engine.world))
    (matter/Events.on mouseConstraint "mouseup" (partial mouseUp scene engine.world))
    (doseq [obj objects]
      (register-obj scene obj))
    #_(matter/Composite.add scene.physics.world (mapv :body (filterv #(contains? % :body) (:objects scene))))
    #_(matter/Composite.add scene.physics.world (into [] (mapcat (comp vals :bodies) (filterv #(contains? % :bodies) (:objects scene)))))
    scene))
