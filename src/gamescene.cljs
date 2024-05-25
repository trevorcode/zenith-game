(ns gamescene
  (:require-macros [macros :refer [def-method]])
  (:require
   [gamestate :refer [update-scene render-scene] :as gs]
   [engine.input :as input]
   [engine.assets :as assets]
   [rune :as rune]
   [ceiling :as ceiling]
   [rope :as rope]
   [button :as button]
   [uiutils :as ui]
   [util :as util]
   ["matter-js" :as matter]
   [engine.animation :as animation]
   [engine.assets :as ea]
   [homescene :as homescene]))

(defn draw-heart [ctx x y]
  (let [image (get-in assets/images [:heart :image])]
    (ctx.setTransform 3 0 0 3 x y)
    (set! ctx.filter "brightness(1.7) saturate(50%) contrast(150%)
                      drop-shadow(0px 0px 10px black) opacity(90%)")
    (ctx.drawImage image (/ (- image.width) 2) (/ (- image.height) 2))
    (ctx.setTransform 1 0 0 1 0 0)
    (set! ctx.filter "none")))

(defn draw-life [scene context]
  (doseq [i (range scene.lives)]
    (draw-heart context (+ (* i 30) 20) (- (get-in gs/game-state [:canvas :height]) 20))))

(defn draw-score [scene context]
  (set! context.font "24px Arial")
  (set! context.lineWidth 4)
  (set! context.strokeStyle "black")
  (context.strokeText (str "Score: " scene.score) 5 (- (get-in gs/game-state [:canvas :height]) 40))
  (context.fillText (str "Score: " scene.score) 5 (- (get-in gs/game-state [:canvas :height]) 40)))

(def-method render-scene :game
  [scene context]
  (animation/draw-image context (get-in assets/images [:bg :image]) {:x 400 :y 400 :scale 1.7 :rotation 0})
  (doseq [game-obj (sort-by :renderIndex (:objects scene))]
    (gs/render-entity game-obj context))

  (let [mousePosition (-> scene :mouse :mouse :position)
        selectedRunePos (-> scene :selectedRune :body :position)]
    (when selectedRunePos
      (set! context.lineWidth 5)
      (set! context.strokeStyle "black")
      (context.beginPath)
      (context.moveTo (:x selectedRunePos) (:y selectedRunePos))
      (context.lineTo (:x mousePosition) (:y mousePosition))
      (context.stroke)))
  (draw-life scene context)
  (draw-score scene context)

  (when scene.gameOver
    (ui/draw-card context)
    (set! context.font "38px Arial")
    (set! context.textAlign "center")
    (set! context.textBaseline "middle")
    (set! context.fillStyle "black")
    (context.fillText "Game Over"
                      (/ (-> gs/game-state :canvas :width) 2)
                      (- (/ (-> gs/game-state :canvas :width) 2) 240))

    (set! context.font "32px Arial")
    (context.fillText (str "Score: " scene.score)
                      (/ (-> gs/game-state :canvas :width) 2)
                      (- (/ (-> gs/game-state :canvas :width) 2) 190))
    (doseq [game-obj (:ui scene)]
      (gs/render-entity game-obj context))))

(defn register-obj [scene obj]
  (conj! scene.objects obj)
  (when (:bodies obj)
    (matter/Composite.add scene.physics.world (:bodies obj)))
  (when (:body obj)
    (matter/Composite.add scene.physics.world (:body obj))))

(declare scene1)
(defn init-game-over [scene]
  (matter/Runner.stop scene.runner)
  (set! scene.gameOver true)
  (ea/play-audio :ending {})
  (->> (button/create {:x (/ (-> gs/game-state :canvas :width) 2)
                       :y (/ (-> gs/game-state :canvas :height) 2)
                       :width 170
                       :height 70
                       :text "Play again"
                       :action (fn []
                                 (set! (.-currentScene gs/game-state) (scene1))
                                 (assets/play-audio :start {}))})

       (conj! scene.ui))
  (->> (button/create {:x (/ (-> gs/game-state :canvas :width) 2)
                       :y (+ (/ (-> gs/game-state :canvas :height) 2) 84)
                       :width 170
                       :height 70
                       :text "Home"
                       :action (fn []
                                 (set! (.-currentScene gs/game-state) (homescene/create)))})

       (conj! scene.ui)))

(defn nextSpawnTime [gameTime]
  (let [doubleSpawn? (= 0 (util/random-between-int 0 5))]
    (if doubleSpawn?
      0.5
      (cond
        (> gameTime 270)
        (util/random-between 0.5 1.5)

        (> gameTime 240)
        (util/random-between 0.5 2)

        (> gameTime 210)
        (util/random-between 0.5 3)

        (> gameTime 180)
        (util/random-between 1 3)

        (> gameTime 120)
        (util/random-between 1 4)

        (> gameTime 60)
        (util/random-between 2 5)

        (> gameTime 30)
        (util/random-between 2.5 6)

        :else
        (util/random-between 3 7)))))

(def-method update-scene :game
  [scene dt]
  (when (and (<= scene.lives 0)
             (= scene.gameOver false))
    (init-game-over scene))

  (if scene.gameOver
    (doseq [game-obj (:ui scene)]
      (gs/update-entity game-obj dt))
    (do
      (set! scene.gameTime (+ scene.gameTime dt))
      (doseq [game-obj (:objects scene)]
        (gs/update-entity game-obj dt))

      (set! scene.nextSpawn (- scene.nextSpawn dt))
      (when (<= scene.nextSpawn 0)
        (set! scene.nextSpawn (nextSpawnTime scene.gameTime))
        (register-obj scene (rune/spawn-rune))))))

(defn calculate-points [scene]
  (let [activatedRunes (filterv #(and (= :rune (:type %))
                                      (:activated %)) scene.objects)
        runetypes  (mapv :runetype activatedRunes)]
    (when (>= (count activatedRunes) 3)
      (cond
        (= 1 (count (set runetypes)))
        (do
          (set! scene.score (+ scene.score 3))
          (assets/play-audio :success {})
          (doseq [rune activatedRunes]
            (rune/set-successful rune)))

        (and (= 4 (count activatedRunes))
             (= (count activatedRunes) (count (set runetypes))))
        (do
          (set! scene.score (+ scene.score 6))
          (set! scene.lives (inc scene.lives))
          (assets/play-audio :success {})
          (doseq [rune activatedRunes]
            (rune/set-successful rune)))

        (= (count activatedRunes) (count (set runetypes)))
        nil

        :else (do
                (assets/play-audio :fail {})
                (doseq [rune activatedRunes]
                  (set! rune.wrongChoiceTimer 50)
                  (set! rune.activated false)))))))


(defn mouseDown [scene world ev]
  (let [objectBodies (filterv some? (mapv #(get % :body) scene.objects))
        clickedObjects (filterv
                        (fn [obj]
                          (some #(= % (:body obj))
                                (matter/Query.point objectBodies ev.mouse.position)))
                        scene.objects)]
    (doseq [clickedObject clickedObjects]
      (case (:type clickedObject)
        :rune (set! scene.selectedRune clickedObject)
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
        :rune (when (not clickedObject.successfulComboTimer)
                (set! clickedObject.activated (not clickedObject.activated))
                (matter/Body.setVelocity clickedObject.body {:x 0 :y -2})
                (calculate-points scene))

        :ceiling (when scene.selectedRune
                   (register-obj scene (rope/create {:x ev.mouse.position.x
                                                     :y ev.mouse.position.y
                                                     :target scene.selectedRune})))
        nil)))
  (set! scene.selectedRune nil))

(defn mouseMove [scene ev]
  (let [objectBodies (filterv some? (mapv #(get % :body) scene.objects))
        hoveredObjects (filterv
                        (fn [obj]
                          (some #(= % (:body obj))
                                (matter/Query.point objectBodies ev.mouse.position)))
                        scene.objects)]

    (doseq [hoveredObject hoveredObjects]
      (case (:type hoveredObject)
        :rune (set! hoveredObject.hoverTimer 100)
        nil))))

(defn onCollisionStart [scene ev]
  (let [runes (filterv #(= :rune (:type %)) scene.objects)
        collidedPairs (filterv
                       (fn [obj]
                         (some #(or (= (:bodyA %) (:body obj))
                                    (= (:bodyB %) (:body obj))) ev.pairs))
                       runes)]
    (when (= 2 (count collidedPairs))
      (doseq [rune runes]
        (when (> rune.body.speed 2)
          (ea/play-audio (str "rockCollide" (util/random-between-int 1 4))
                         {:volume (min 1.0
                                       (/ rune.body.speed 8))}))))))

(defn scene1 []
  (let [engine (matter/Engine.create)
        runner (matter/Runner.create)
        objects [(ceiling/create)]
        mouse (matter/Mouse.create (:canvas gs/game-state))
        mouseConstraint (matter/MouseConstraint.create engine
                                                       {:mouse mouse
                                                        :constraint {:stiffness 0}})
        scene {:type :scene
               :id :game
               :gameTime 0
               :objects []
               :ui []
               :score 0
               :lives 3
               :nextSpawn 3.5
               :gameOver false
               :mouse mouseConstraint
               :physics engine
               :runner runner}]

    (matter/Runner.run runner engine)
    (set! engine.gravity.y 0.2)
    (matter/Composite.add engine.world mouseConstraint)
    (matter/Events.on mouseConstraint "mousedown" (partial mouseDown scene engine.world))
    (matter/Events.on mouseConstraint "mouseup" (partial mouseUp scene engine.world))
    (matter/Events.on mouseConstraint "mousemove" (partial mouseMove scene))
    (matter/Events.on engine "collisionStart" (partial onCollisionStart scene))
    (doseq [obj objects]
      (register-obj scene obj))
    scene))
