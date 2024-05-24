(ns rune
  (:require-macros [macros :refer [def-method]])
  (:require [engine.animation :refer [draw-image draw-image-physics] :as animation]
            [engine.assets :as assets]
            [gamestate :refer [render-entity update-entity] :as gamestate]
            [rope :as rope]
            ["matter-js" :as matter]))

(assets/register-animations
 {:rune0 {:sheet :runesheet
          :height 32
          :width 32
          :duration 10
          :durationCounter 0
          :frame 0
          :rows 3
          :columns 2
          :cells [0]
          :loop false}

  :rune1 {:sheet :runesheet
          :height 32
          :width 32
          :duration 10
          :durationCounter 0
          :frame 0
          :rows 3
          :columns 2
          :cells [1]
          :loop false}
  :rune2 {:sheet :runesheet
          :height 32
          :width 32
          :duration 10
          :durationCounter 0
          :frame 0
          :rows 3
          :columns 2
          :cells [2]
          :loop false}

  :rune3 {:sheet :runesheet
          :height 32
          :width 32
          :duration 10
          :durationCounter 0
          :frame 0
          :rows 3
          :columns 2
          :cells [3]
          :loop false}})

(defn set-successful [rune]
  (set! rune.successfulComboTimer 100)
  (doseq [rope rune.ropes]
    (println rope)
    (set! rope.successfulComboTimer 100))
  (set! rune.body.collisionFilter.mask 0)
  (set! rune.activated false))

(defn destroy-rune [scene {:keys [id body ropes] :as rune}]
  (when (and scene.selectedRune
             (= scene.selectedRune.id id))
    (set! scene.selectedRune nil))
  (doseq [rope ropes]
    (rope/destroy-rope scene rope))
  (matter/Composite.remove scene.physics.world body true)
  (set! scene.objects
        (filterv #(not= (:id %) id) scene.objects)))

(def-method update-entity :rune [{:keys [body id wrongChoiceTimer successfulComboTimer] :as rune} dt]
  (let [gs gamestate/game-state
        scene gs.currentScene
        heightBuffer 300]
    (when (and (pos? body.velocity.y)
               (> body.position.y (+ heightBuffer gs.canvas.height)))
      (when (not successfulComboTimer)
        (set! scene.lives (dec scene.lives)))
      (destroy-rune scene rune))

    (when wrongChoiceTimer
      (if (> wrongChoiceTimer 0)
        (set! rune.wrongChoiceTimer (dec wrongChoiceTimer))
        (set! rune.wrongChoiceTimer nil)))

    (when successfulComboTimer
      (if (> successfulComboTimer 0)
        (set! rune.successfulComboTimer (dec successfulComboTimer))
        (destroy-rune scene rune)))))

(def-method render-entity :rune [{:keys [animation]
                                  :as this} ctx]
  (cond
    (:activated this)
    (do (set! ctx.filter "brightness(1.7) saturate(100%) hue-rotate(130deg) contrast(150%)
                          drop-shadow(0px 0px 20px yellow)")
        (animation/draw-animation-physics this animation ctx)
        (set! ctx.filter "none"))

    (:wrongChoiceTimer this)
    (do (set! ctx.filter "brightness(1.0) saturate(100%) hue-rotate(65deg) contrast(150%)
                          drop-shadow(0px 0px 0px red)")
        (animation/draw-animation-physics this animation ctx)
        (set! ctx.filter "none"))

    (:successfulComboTimer this)
    (do (set! ctx.filter (str "brightness("
                              (+ 0.5 (js/Math.pow (- 100 this.successfulComboTimer) 0.25))
                              ") 
                              saturate(100%) hue-rotate(240deg) contrast(150%)
                              opacity(" this.successfulComboTimer "%)
                          drop-shadow(0px 0px 20px white)"))
        (animation/draw-animation-physics this animation ctx)
        (set! ctx.filter "none"))

    :else (animation/draw-animation-physics this animation ctx)))

(def rune-type {0 :earth
                1 :wind
                2 :tree
                3 :rain})

(defn create [{:keys [x y runetype]}]
  (let [body (matter/Bodies.rectangle x y 80 80)]
    (-> {:type :rune
         :id (* 200000 (js/Math.random))
         :body body
         :runetype (get rune-type (or runetype 0))
         :activated false
         :ropes []
         :renderIndex 5
         :scale 3}
        (animation/play-animation (str "rune" (or runetype 0))))))

(defn spawn-rune []
  (let [width gamestate/game-state.canvas.width
        height gamestate/game-state.canvas.height
        rune (create {:x (* (js/Math.random) width)
                      :y height
                      :runetype (int (* (js/Math.random) 4))})
        vx (/ (- (/ width 2) rune.body.position.x) (/ width 21))]

    (matter/Body.setVelocity rune.body {:x vx :y -13})
    rune))
