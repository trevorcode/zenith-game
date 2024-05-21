(ns engine.core)

(def state
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

(defn inititalize []
  )