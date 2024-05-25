(ns text
  (:require-macros [macros :refer [def-method]])
  (:require [gamestate :refer [render-entity] :as gs]))

(def-method render-entity :text [{:keys [x y text]
                                  :as obj} ctx]
  (set! ctx.fillStyle "black")
  (set! ctx.font "18px Arial")
  (set! ctx.textAlign "center")
  (set! ctx.textBaseline "middle")
  (ctx.fillText text x y))

(defn create [{:keys [x y text]}]
  {:type :text
   :id (* 200000 (js/Math.random))
   :text text
   :x x
   :y y })
