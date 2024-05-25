(ns uiutils
  (:require [gamestate :as gs]
            [engine.animation :as animation]
            [engine.assets :as assets]))

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

(defn draw-zenith-logo [context]
  (set! context.filter "hue-rotate(-45deg) saturate(0.5)")
  (animation/draw-image context (get-in assets/images [:zenith :image]) 
                        {:x (/ (-> gs/game-state :canvas :width) 2) 
                         :y (- (/ (-> gs/game-state :canvas :height) 2) 120)
                         :scale 1.0 :rotation 0})
  (set! context.filter "none"))
    