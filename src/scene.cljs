(ns scene
  (:require
   [gamestate :as gs]
   [engine.input :as input]
   [engine.assets :as assets]
   [rune :as rune]

   [ceiling :as ceiling]
   [rope :as rope]
   ["matter-js" :as matter]
   [engine.animation :as animation]))

(defn draw-life [scene context]
  (set! context.font "32px serif")
  (context.fillText (str "Lives " scene.lives) 10 100))

(defn draw-score [scene context]
  (set! context.font "32px serif")
  (context.fillText (str "Score: " scene.score) 10 50))

(defn scene-draw [scene context]
  (animation/draw-image context (get-in assets/images [:bg :image]) {:x 400 :y 400 :scale 1.7 :rotation 0})
  (doseq [game-obj (sort-by :renderIndex (:objects scene))]
    (gs/render-entity game-obj context))

  (let [mousePosition (-> scene :mouse :mouse :position)
        selectedRunePos (-> scene :selectedRune :body :position)]
    (when selectedRunePos
      (context.beginPath)
      (context.moveTo (:x selectedRunePos) (:y selectedRunePos))
      (context.lineTo (:x mousePosition) (:y mousePosition))
      (context.stroke)))
  (draw-life scene context)
  (draw-score scene context))

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

  (when (> (* (js/Math.random) 1000) 997)
    (register-obj scene (rune/spawn-rune))))

(defn calculate-points [scene]
  (let [activatedRunes (filterv #(and (= :rune (:type %))
                                      (:activated %)) scene.objects)
        runetypes  (mapv :runetype activatedRunes)]
    (when (>= (count activatedRunes) 3)
      (cond
        (= 1 (count (set runetypes)))
        (do
          (set! scene.score (+ scene.score 3))
          (doseq [rune activatedRunes]
            (rune/set-successful rune)))

        (and (= 4 (count activatedRunes))
             (= (count activatedRunes) (count (set runetypes))))
        (do
          (set! scene.score (+ scene.score 6))
          (set! scene.lives (inc scene.lives))
          (doseq [rune activatedRunes]
            (rune/set-successful rune)))

        (= (count activatedRunes) (count (set runetypes)))
        nil

        :else (doseq [rune activatedRunes]
                (set! rune.wrongChoiceTimer 50)
                (set! rune.activated false))))))


(defn mouseDown [scene world ev]
  (let [objectBodies (filterv some? (mapv #(get % :body) scene.objects))
        clickedObjects (filterv
                        (fn [obj]
                          (some #(= % (:body obj))
                                (matter/Query.point objectBodies ev.mouse.position)))
                        scene.objects)]
    (doseq [clickedObject clickedObjects]
      (case (:type clickedObject)
        :rune (do
                (set! scene.selectedRune clickedObject))
        nil))))

(defn mouseUp [scene world ev]
  (let [objectBodies (filterv some? (mapv #(get % :body) scene.objects))
        clickedObjects (filterv
                        (fn [obj]
                          (some #(= % (:body obj))
                                (matter/Query.point objectBodies ev.mouse.position)))
                        scene.objects)]
    (doseq [clickedObject clickedObjects]
      (case (:type clickedObject)
        :rune (do
                (set! clickedObject.activated (not clickedObject.activated))
                (matter/Body.setVelocity clickedObject.body {:x 0 :y -2})
                (calculate-points scene))

        :ceiling (when scene.selectedRune
                   (register-obj scene (rope/create {:x ev.mouse.position.x
                                                     :y ev.mouse.position.y
                                                     :target scene.selectedRune})))
        nil)))

  (set! scene.selectedRune nil))

(defn scene1 []

  (let [engine (let [engine (matter/Engine.create)
                     ground (matter/Bodies.rectangle 400 610 810 90 {:isStatic true})
                     #_#_render (matter/Render.create {:element js/document.body
                                                   :engine engine})]
                 #_(matter/Composite.add engine.world ground)
                 #_(matter/Render.run render)
                 (matter/Runner.run (matter/Runner.create) engine)
                 engine)
        objects (into [(rune/create {:x 300 :y 50 :rotation 10})
                       (rune/create {:x 400 :y 80 :rotation 90})
                       (rune/create {:x 450 :y 80 :rotation 90})
                       (ceiling/create)])
        mouse (matter/Mouse.create (:canvas gs/game-state))
        mouseConstraint (matter/MouseConstraint.create engine
                                                       {:mouse mouse
                                                        :constraint {:stiffness 0}})
        scene {:type :scene
               :dt 0
               :objects []
               :score 0
               :lives 3
               :mouse mouseConstraint
               :physics engine}]

    (set! engine.gravity.y 0.4)
    (matter/Composite.add engine.world mouseConstraint)
    (matter/Events.on mouseConstraint "mousedown" (partial mouseDown scene engine.world))
    (matter/Events.on mouseConstraint "mouseup" (partial mouseUp scene engine.world))
    (doseq [obj objects]
      (register-obj scene obj))
    scene))
