(ns button
  (:require-macros [macros :refer [def-method]])
  (:require [gamestate :refer [render-entity update-entity] :as gs]))

(defn buttonClicked? [button]
  (let [x gs/game-state.mouse.x
        y gs/game-state.mouse.y
        btnX (- button.x (/ button.width 2))
        btnY (- button.y (/ button.height 2))]
    (and gs/game-state.mouse.mouse1
         (>= x btnX)
         (>= y btnY)
         (<= x (+ btnX button.width))
         (<= y (+ btnY button.height)))))

(defn hover? [button]
  (let [x gs/game-state.mouse.x
        y gs/game-state.mouse.y
        btnX (- button.x (/ button.width 2))
        btnY (- button.y (/ button.height 2))]
    (and (>= x btnX)
         (>= y btnY)
         (<= x (+ btnX button.width))
         (<= y (+ btnY button.height)))))

(def-method update-entity :button [button dt]
  (when (buttonClicked? button)
    ((:action button)))

  (set! button.state :waiting)
  (when (hover? button)
    (set! button.state :hover)))

(def-method render-entity :button [{:keys [x y width height
                                           state text]
                                    :as button} ctx]
  (set! ctx.fillStyle "#A8C8A6")
  (when (= :hover button.state)
    (set! ctx.fillStyle "#B8D8B6"))
  (ctx.fillRect  (- x (/ width 2))
                 (- y (/ height 2))
                 width
                 height)

  (set! ctx.fillStyle "black")

  (set! ctx.font "24px Arial")
  (set! ctx.textAlign "center")
  (set! ctx.textBaseline "middle")
  (ctx.fillText text x y))

(defn create [{:keys [x y width height action text text-offset-x text-offset-y]}]
  {:type :button
   :id (* 200000 (js/Math.random))
   :action action
   :state :waiting
   :text text
   :text-offset-x (or text-offset-x 0)
   :text-offset-y (or text-offset-y 0)
   :x x
   :y y
   :width width
   :height height})
