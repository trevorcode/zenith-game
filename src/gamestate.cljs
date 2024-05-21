(ns gamestate
  (:require-macros [macros :refer [def-multi def-method]])
  (:require [macros :as macros]))

(def game-state
  {:dt 0
   :lastUpdate 0
   :mouse {:x 0
           :y 0
           :btn [false false false]}
   :keyboard #{}
   :canvas nil
   :context nil
   :loading true
   :currentScene {:objects []}})

(def-multi render-entity (fn [x] (:type x)))
(def-method render-entity :default [])
(def-multi update-entity (fn [x] (:type x)))
(def-method update-entity :default [])
