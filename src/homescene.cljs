(ns scene
  (:require-macros [macros :refer [def-method]])
  (:require
   [gamestate :refer [update-scene render-scene] :as gs]
   [button :as button]
   [engine.assets :as assets]
   [uiutils :as ui]
   [gamescene :as gamescene]
   [instructions :as instructions]
   ["matter-js" :as matter]
   [engine.animation :as animation]))

(def-method render-scene :main
  [scene context]
  (animation/draw-image context (get-in assets/images [:bg :image]) {:x 400 :y 400 :scale 1.7 :rotation 0})

  (ui/draw-card context)
  (ui/draw-zenith-logo context)
  (doseq [game-obj (sort-by :renderIndex (:objects scene))]
    (gs/render-entity game-obj context)))

(def-method update-scene :main
  [scene dt]
  (doseq [game-obj (:objects scene)]
    (gs/update-entity game-obj dt)))

(defn create []
  (let [objects [(button/create {:x (/ (-> gs/game-state :canvas :width) 2)
                                 :y (/ (-> gs/game-state :canvas :height) 2)
                                 :width 170
                                 :height 70
                                 :text "Play"
                                 :action (fn []
                                           (set! (.-currentScene gs/game-state) (gamescene/scene1))
                                           (assets/play-audio :start {}))})
                 (button/create {:x (/ (-> gs/game-state :canvas :width) 2)
                                 :y (+ (/ (-> gs/game-state :canvas :height) 2) 85)
                                 :width 170
                                 :height 70
                                 :text "Instructions"
                                 :action (fn []
                                           (set! (.-currentScene gs/game-state) (instructions/create)))})]
        scene {:type :scene
               :id :main
               :objects objects}]
    scene))
