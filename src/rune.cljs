(ns rune
  (:require-macros [macros :refer [def-method]])
  (:require [engine.animation :refer [draw-image draw-image-physics] :as animation]
            [engine.assets :as assets]
            [gamestate :refer [render-entity update-entity] :as gamestate]
            [rope :as rope]
            ["matter-js" :as matter]))

(assets/register-animations
 {:rune1 {:sheet :runesheet
          :height 32
          :width 32
          :duration 10
          :durationCounter 0
          :frame 0
          :rows 3
          :columns 2
          :cells [1]
          :loop false}})

(def-method update-entity :rune [{:keys [transform]} dt])

(def-method render-entity :rune [{:keys [animation]
                                  :as this} ctx]
  (animation/draw-animation-physics this animation ctx))

(defn create [{:keys [x y rotation]}]
  (let [body (matter/Bodies.rectangle x y 80 80)]
    (-> {:type :rune
         :body body
         :activated false
         :ropes []
         :renderIndex 5
         :scale 3}
        (animation/play-animation :rune1))))

(defn spawn-rune []
  (let [width gamestate/game-state.canvas.width
        height gamestate/game-state.canvas.height]
    (create {:x (* (js/Math.random) width) :y height})))
