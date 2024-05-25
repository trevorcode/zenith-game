(ns main
  (:require
   [engine.assets :as ea]
   [assets :as assets]
   [gamestate :as gs]
   [engine.input :as input]
   [homescene :as homescene]
   ["matter-js" :as matter]))

(defn create-canvas! [id w h]
  (let [id-string (str "#canvas_" id)
        try-canv (js/document.querySelector id-string)
        canv (if try-canv
               try-canv
               (js/document.createElement "canvas"))]
    (set! (.-width canv) w)
    (set! (.-height canv) h)
    (set! (.-id canv) id-string)
    canv))

(defn create-context! [canvas]
  (let [context (canvas.getContext "2d")]
    (set! (.-imageSmoothingEnabled context) false)
    context))

(defn load []
  (ea/register-audio {:fireball {:url "assets/foom_0.wav"
                                 :type :static}
                      :success {:url "assets/success.wav"
                                :type :static}
                      :fail {:url "assets/fail.wav"
                             :type :static}
                      :bomp {:url "assets/bomp.wav"
                             :type :static}
                      :start {:url "assets/start.wav"
                             :type :static}
                      :ending {:url "assets/ending.wav"
                             :type :static}
                      })
  (ea/load-audios)
  #_(ea/play-audio :fireball)

  (ea/register-images assets/unloaded-images)
  (ea/load-images))

(defn draw [{:keys [canvas context currentScene]}]
  (set! (.-fillStyle context) "#f0f0e2")
  (context.fillRect 0 0 canvas.width canvas.height)
  (gs/render-scene currentScene context))

(defn game-update [{:keys [currentScene dt]}]
  (gs/update-scene currentScene dt))

(defn main-loop [game-state time]
  (set! game-state.dt (/ (- time (:lastUpdate game-state)) 1000))
  (set! game-state.lastUpdate time)

  (.save (:context game-state))
  (game-update game-state)
  (draw game-state)
  (.restore (:context game-state))
  (set! gs/game-state.mouse.mouse1 false)

  (js/window.requestAnimationFrame (partial main-loop game-state)))

(defn init-game []
  (let [canvas (create-canvas! "2" 800 600)
        context (create-context! canvas)]

    (input/subscribe-to-keyboard-events gs/game-state)

    (set! gs/game-state.canvas canvas)
    (set! gs/game-state.context context)
    (-> (js/document.querySelector "#app")
        (.append canvas))
    (canvas.addEventListener "click" (fn [{:keys [offsetX offsetY] :as event}]
                                       (set! gs/game-state.mouse.x offsetX)
                                       (set! gs/game-state.mouse.y offsetY)
                                       (set! gs/game-state.mouse.mouse1 true)))

    (canvas.addEventListener "mousemove" (fn [{:keys [offsetX offsetY] :as event}]
                                           (set! gs/game-state.mouse.x offsetX)
                                           (set! gs/game-state.mouse.y offsetY))))

  (load)
  (println gs/game-state)

  (set! (.-currentScene gs/game-state) (homescene/create))

  (js/window.requestAnimationFrame (partial main-loop gs/game-state)))

(init-game)

