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

(defn destroy-rune [scene {:keys [id body ropes] :as rune}]
  (when (and scene.selectedRune
             (= scene.selectedRune.id id))
    (set! scene.selectedRune nil))
  (doseq [rope ropes]
    (rope/destroy-rope scene rope))
  (matter/Composite.remove scene.physics.world body true)
  (set! scene.objects
        (filterv #(not= (:id %) id) scene.objects)))

(def-method update-entity :rune [{:keys [body id] :as rune} dt]
  (let [gs gamestate/game-state
        scene gs.currentScene
        heightBuffer 200]
    (when (and (pos? body.velocity.y)
               (> body.position.y (+ heightBuffer gs.canvas.height)))
      (destroy-rune scene rune)
      (println scene))

    (when (:activated rune)
      (destroy-rune scene rune))))

(def-method render-entity :rune [{:keys [animation]
                                  :as this} ctx]
  (animation/draw-animation-physics this animation ctx))

(def rune-type {0 :earth
                1 :wind
                2 :tree
                3 :rain})

(defn create [{:keys [x y runetype]}]
  (let [body (matter/Bodies.rectangle x y 80 80)]
    (-> {:type :rune
         :id (* 200000 (js/Math.random))
         :body body
         #_#_:runetype (get rune-type runetype)
         :activated false
         :ropes []
         :renderIndex 5
         :scale 3}
        (animation/play-animation (str "rune" (or runetype 0))))))

(defn spawn-rune []
  (let [width gamestate/game-state.canvas.width
        height gamestate/game-state.canvas.height
        rune (create {:x (* (js/Math.random) width)
                      :y (- height 100)
                      :runetype (int (* (js/Math.random) 3))})
        vx (/ (- (/ width 2) rune.body.position.x) (/ width 21))]
    (println (int (* (js/Math.random) 3)))

    (matter/Body.setVelocity rune.body {:x vx :y -13})
    rune))
