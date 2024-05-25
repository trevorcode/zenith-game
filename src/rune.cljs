(ns rune
  (:require-macros [macros :refer [def-method]])
  (:require [engine.animation :as animation]
            [engine.assets :as assets]
            [gamestate :refer [render-entity update-entity] :as gamestate]
            [rope :as rope]
            [util :as util]
            ["matter-js" :as matter]))

(def rune-type {0 :earth
                1 :wind
                2 :tree
                3 :rain})

(defn rune-type-cell [runetype]
  (get {:earth 0
        :wind 1
        :tree 2
        :rain 3}
       runetype))

(def rune-hue-rotation {0 340
                        1 85
                        2 180
                        3 280})

(def rune-saturation {0 0.9
                      1 0.7
                      2 1.0
                      3 1.0})

(defn set-successful [rune]
  (set! rune.successfulComboTimer 100)
  (doseq [rope rune.ropes]
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

(def-method update-entity :rune [{:keys [body
                                         wrongChoiceTimer
                                         successfulComboTimer
                                         hoverTimer] :as rune} dt]
  (let [gs gamestate/game-state
        scene gs.currentScene
        heightBuffer 300]
    (when (and (pos? body.velocity.y)
               (> body.position.y (+ heightBuffer gs.canvas.height)))
      (when (not successfulComboTimer)
        (assets/play-audio :bomp {})
        (set! scene.lives (dec scene.lives)))
      (destroy-rune scene rune))

    (when wrongChoiceTimer
      (if (> wrongChoiceTimer 0)
        (set! rune.wrongChoiceTimer (dec wrongChoiceTimer))
        (set! rune.wrongChoiceTimer nil)))

    (when successfulComboTimer
      (if (> successfulComboTimer 0)
        (set! rune.successfulComboTimer (dec successfulComboTimer))
        (destroy-rune scene rune)))

    (when hoverTimer
      (if (> hoverTimer 0)
        (set! rune.hoverTimer (dec hoverTimer))
        (set! rune.hoverTimer nil)))))

(def-method render-entity :rune [{:keys [runetype]
                                  :as this} ctx]

  (let [runetype-num (rune-type-cell runetype)
        baseFilter (str "hue-rotate(" (get rune-hue-rotation runetype-num) "deg) "
                        "saturate(" (get rune-saturation runetype-num) ")")
        runeFilter
        (cond (:activated this)
              "brightness(1.7) saturate(100%) hue-rotate(130deg) 
               contrast(150%) drop-shadow(0px 0px 20px yellow)"

              (:wrongChoiceTimer this)
              "brightness(1.0) saturate(100%) hue-rotate(65deg) 
               contrast(150%) drop-shadow(0px 0px 0px red)"

              (:successfulComboTimer this)
              (str "brightness(" (+ 0.5 (js/Math.pow (- 100 this.successfulComboTimer) 0.25)) ")"
                   "saturate(100%) hue-rotate(240deg) contrast(150%)
                    opacity(" this.successfulComboTimer "%)
                    drop-shadow(0px 0px 20px white)")

              (:hoverTimer this)
              (str baseFilter
                   "drop-shadow(0px 0px " (/ this.hoverTimer 5) "px white) ")

              :else baseFilter)]
    (animation/draw-image-cell ctx (get-in assets/images [:runesheet :image])
                               {:x (-> this :body :position :x)
                                :y (-> this :body :position :y)
                                :scale (:scale this)
                                :rotation (-> this :body :angle)
                                :columns 2
                                :cell (rune-type-cell runetype)
                                :width 32
                                :height 32
                                :filter runeFilter})))


(defn create [{:keys [x y runetype]}]
  (let [body (matter/Bodies.rectangle x y 80 80)
        runetype (or runetype 0)]
    (-> {:type :rune
         :id (* 200000 (js/Math.random))
         :body body
         :runetype (get rune-type runetype)
         :activated false
         :ropes []
         :hueOffset (get rune-hue-rotation runetype)
         :saturation (get rune-saturation runetype)
         :renderIndex 5
         :scale 3})))

(defn spawn-rune []
  (let [width gamestate/game-state.canvas.width
        height gamestate/game-state.canvas.height
        rune (create {:x (util/random-between 0 width)
                      :y height
                      :runetype (js/Math.floor (util/random-between 0 4))})
        vx (/ (- (/ width 2) rune.body.position.x) (/ width (util/random-between 0 21)))]

    (matter/Body.setVelocity rune.body {:x vx :y -11})
    rune))
