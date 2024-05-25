(ns scene
  (:require-macros [macros :refer [def-method]])
  (:require
   [gamestate :refer [update-scene render-scene] :as gs]
   [button :as button]
   [engine.input :as input]
   [engine.assets :as assets]
   [rune :as rune]
   [gamescene :as gamescene]
   [ceiling :as ceiling]
   ["matter-js" :as matter]
   [engine.animation :as animation]))

(defn draw-card [context]
  (let [canvas-width gs/game-state.canvas.width
        canvas-height gs/game-state.canvas.height
        width (* 0.7 canvas-width)
        height (* 0.7 canvas-height)]
    (set! context.filter "opacity(70%) ")
    (context.fillRect (- (/ canvas-width 2) (/ width 2))
                      (- (/ canvas-height 2) (/ height 2))
                      width
                      height)
    (set! context.filter "none")))

(def-method render-scene :main
  [scene context]
  (animation/draw-image context (get-in assets/images [:bg :image]) {:x 400 :y 400 :scale 1.7 :rotation 0})
  (draw-card context)

  (doseq [game-obj (sort-by :renderIndex (:objects scene))]
    (gs/render-entity game-obj context)))

(def-method update-scene :main
  [scene dt]
  (set! scene.dt (inc scene.dt))
  (doseq [game-obj (:objects scene)]
    (gs/update-entity game-obj dt)))

(defn create []
  (let [objects [(button/create {:x (/ (-> gs/game-state :canvas :width) 2)
                                 :y (/ (-> gs/game-state :canvas :height) 2)
                                 :width 170
                                 :height 70
                                 :text-offset-x -25
                                 :text-offset-y 3
                                 :text "Play"
                                 :action (fn [] 
                                           (set! (.-currentScene gs/game-state) (gamescene/scene1))
                                           (assets/play-audio :start))})]
        scene {:type :scene
               :id :main
               :dt 0
               :objects objects}]
    scene))
