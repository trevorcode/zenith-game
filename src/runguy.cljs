(ns runguy
  (:require-macros [macros :refer [def-method]])
  (:require [engine.animation :as animation]
            [engine.assets :as ea]
            [engine.input :as input]
            [gamestate :refer [render-entity update-entity]]
            [transform :as transform]))

(def-method update-entity :runguy
  [{:keys [transform]} _dt]
  (when (input/key-down? (:A input/keys))
    (set! (.-x transform) (dec (.-x transform))))
  (when (input/key-down? (:D input/keys))
    (set! (.-x transform) (inc (.-x transform))))
  (when (input/key-down? (:W input/keys))
    (set! (.-y transform) (dec (.-y transform))))
  (when (input/key-down? (:S input/keys))
    (set! (.-y transform) (inc (.-y transform)))
    (ea/play-audio :fireball)))

(def-method render-entity :runguy
  [this ctx]
  (let [current-animation (get-in this [:animation-component :current-animation])
        animation (get-in this [:animation-component :animations current-animation])]
    (animation/draw-animation this animation ctx)))

(defn runguy-anim []
  {:sheet :runguy
   :height 500
   :width 210
   :duration 40
   :durationCounter 0
   :frame 0
   :rows 2
   :columns 5
   :cells [0 2 1 2 3 2 1]
   :loop true})

(defn create [{:keys [x y rotation]}]
  (-> {:type :runguy
       :animation-component {:animations {:run (runguy-anim)}
                             :current-animation :run}}
      (transform/attach-transform {:x x 
                                   :y y 
                                   :rotation rotation
                                   :scale 0.5})))


