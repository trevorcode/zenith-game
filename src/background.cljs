(ns background
  (:require-macros [macros :refer [def-method]])
  (:require [engine.animation :refer [draw-image]]
            [engine.assets :as assets]
            [gamestate :refer [render-entity]]
            [transform :as transform]))

(def-method render-entity :bg [{:keys [transform]} ctx]
  (draw-image ctx (get-in assets/images [:bg :image]) transform))

(defn create []
  (-> {:type :bg}
      (transform/attach-transform {:x 250
                                   :y 250
                                   :scale 0.5})))
