(ns image
  (:require-macros [macros :refer [def-method]])
  (:require [gamestate :refer [render-entity] :as gs]
            [engine.animation :as animation]
            [engine.assets :as assets]))

(def-method render-entity :image [{:keys [x y image scale filter
                                          columns cell width height]
                                   :as obj} ctx]

  (when (not= filter "none")
    (set! ctx.filter filter))
  (animation/draw-image-cell ctx
                             (:image image)
                             {:x x
                              :y y
                              :scale scale
                              :rotation 0
                              :columns columns
                              :cell cell
                              :width width
                              :height height})
  (set! ctx.filter "none"))

(defn create [{:keys [x y imageKeyword scale columns cell width height filter]}]
  (let [image (get-in assets/images [imageKeyword])]
    {:type :image
     :id (* 200000 (js/Math.random))
     :image image
     :x x
     :y y
     :columns (or columns 1)
     :cell (or cell 0)
     :filter (or filter "none")
     :width (or width (-> image :image :width))
     :height (or height (-> image :image :height))
     :scale (or scale 1.0)}))