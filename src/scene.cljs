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
  (doseq [game-obj (sort-by :renderIndex (:objects scene))]
    (gs/render-entity game-obj context))

  (let [mousePosition (-> scene :mouse :mouse :position)
        selectedRunePos (-> scene :selectedRune :body :position)]
    (when selectedRunePos
      (context.beginPath)
      (context.moveTo (:x selectedRunePos) (:y selectedRunePos))
      (context.lineTo (:x mousePosition) (:y mousePosition))
      (context.stroke))))

(defn register-obj [scene obj]
  (conj! scene.objects obj)
  (when (:bodies obj)
    (matter/Composite.add scene.physics.world (:bodies obj)))
  (when (:body obj)
    (matter/Composite.add scene.physics.world (:body obj))))

(defn scene-update [scene dt]
  (set! scene.dt (inc scene.dt))
  (doseq [game-obj (:objects scene)]
    (gs/update-entity game-obj dt))

  (when (> (* (js/Math.random) 500) 495)
    (register-obj scene (rune/spawn-rune))))




(defn mouseDown [scene world ev]
  (let [objectBodies (filterv some? (mapv #(get % :body) scene.objects))
        clickedObject (first (filterv
                              #(= (get % :body)
                                  (first (matter/Query.point objectBodies ev.mouse.position)))
                              scene.objects))]

    (case (:type clickedObject)
      :rune (do
              (set! scene.selectedRune clickedObject))
      nil)))

(defn mouseUp [scene world ev]
  (let [objectBodies (filterv some? (mapv #(get % :body) scene.objects))
        clickedObject (first (filterv
                              #(= (get % :body)
                                  (first (matter/Query.point objectBodies ev.mouse.position)))
                              scene.objects))]
    (case (:type clickedObject)
      :rune (do
              (set! clickedObject.activated (not clickedObject.activated))
              (println clickedObject))

      :ceiling (when scene.selectedRune
                 (register-obj scene (rope/create {:x ev.mouse.position.x
                                                   :y ev.mouse.position.y
                                                   :target scene.selectedRune})))
      nil))

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
        objects (into [(rune/create {:x 300 :y 50 :rotation 10})
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
               :dt 0
               :objects []
               :score 0
               :mouse mouseConstraint
               :physics engine}]

    (matter/Composite.add engine.world mouseConstraint)
    (matter/Events.on mouseConstraint "mousedown" (partial mouseDown scene engine.world))
    (matter/Events.on mouseConstraint "mouseup" (partial mouseUp scene engine.world))
    (doseq [obj objects]
      (register-obj scene obj))
    scene))
