(ns instructions
  (:require-macros [macros :refer [def-method]])
  (:require
   [gamestate :refer [update-scene render-scene] :as gs]
   [button :as button]
   [engine.assets :as assets]
   [uiutils :as ui]
   [gamescene :as gamescene]
   [text :as text]
   [image :as image]
   [engine.animation :as animation]
   [homescene :as homescene]))

(declare select-page)
(defn page1-init [scene]
  (let [verticalCenter (/ (-> gs/game-state :canvas :height) 2)
        horizontalCenter (/ (-> gs/game-state :canvas :width) 2)]
    [(button/create {:x (+ horizontalCenter 90)
                     :y (+ verticalCenter 150)
                     :width 170
                     :height 70
                     :text "Next"
                     :action (fn []
                               (select-page scene 2))})

     (button/create {:x (- horizontalCenter 90)
                     :y (+ verticalCenter 150)
                     :width 170
                     :height 70
                     :text "Home"
                     :action (fn []
                               (set! (.-currentScene gs/game-state) (homescene/create)))})

     (text/create {:x horizontalCenter
                   :y (- verticalCenter 170)
                   :text "Click and drag the runes to the top bar. "})
     (text/create {:x horizontalCenter
                   :y (- verticalCenter 140)
                   :text "This will attach a rope to them."})
     (image/create {:x horizontalCenter
                    :y (- verticalCenter 10)
                    :imageKeyword :instr1
                    :scale 0.7})]))

(defn page2-init [scene]
  (let [verticalCenter (/ (-> gs/game-state :canvas :height) 2)
        horizontalCenter (/ (-> gs/game-state :canvas :width) 2)]
    [(button/create {:x (+ horizontalCenter 90)
                     :y (+ verticalCenter 150)
                     :width 170
                     :height 70
                     :text "Next"
                     :action (fn []
                               (select-page scene 3))})

     (button/create {:x (- horizontalCenter 90)
                     :y (+ verticalCenter 150)
                     :width 170
                     :height 70
                     :text "Home"
                     :action (fn []
                               (set! (.-currentScene gs/game-state) (homescene/create)))})
     (text/create {:x horizontalCenter
                   :y (- verticalCenter 170)
                   :text "Clicking on runes will activate them"})
     (text/create {:x horizontalCenter
                   :y (- verticalCenter 140)
                   :text "By activating a combo of runes, you earn points"})

     (image/create {:x horizontalCenter
                    :y (- verticalCenter 10)
                    :imageKeyword :instr2
                    :scale 0.7})]))

(defn page3-init [scene]
  (let [verticalCenter (/ (-> gs/game-state :canvas :height) 2)
        horizontalCenter (/ (-> gs/game-state :canvas :width) 2)]

    (concat [(button/create {:x horizontalCenter
                             :y (+ verticalCenter 170)
                             :width 170
                             :height 70
                             :text "Home"
                             :action (fn []
                                       (set! (.-currentScene gs/game-state) (homescene/create)))})
             (text/create {:x horizontalCenter
                           :y (- verticalCenter 150)
                           :text "Available combos"})]

            (let [rowHeight 40]
              [(image/create {:x (- horizontalCenter 80)
                              :y (- verticalCenter rowHeight)
                              :imageKeyword :runesheet
                              :columns 2
                              :width 32
                              :filter (str "hue-rotate(" 340 "deg) "
                                           "saturate(" 0.9 ")")
                              :height 32
                              :cell 0
                              :scale 2})
               (image/create {:x (- horizontalCenter 120)
                              :y (- verticalCenter rowHeight)
                              :imageKeyword :runesheet
                              :columns 2
                              :width 32
                              :filter (str "hue-rotate(" 340 "deg) "
                                           "saturate(" 0.9 ")")
                              :height 32
                              :cell 0
                              :scale 2})
               (image/create {:x (- horizontalCenter 160)
                              :y (- verticalCenter rowHeight)
                              :imageKeyword :runesheet
                              :columns 2
                              :width 32
                              :filter (str "hue-rotate(" 340 "deg) "
                                           "saturate(" 0.9 ")")
                              :height 32
                              :cell 0
                              :scale 2})
               (text/create {:x (+ 80 horizontalCenter)
                             :y (- verticalCenter rowHeight)
                             :text "3 of a kind: 3 points"})])

            (let [rowHeight -30]
              [(image/create {:x (- horizontalCenter 60)
                              :y (- verticalCenter rowHeight)
                              :imageKeyword :runesheet
                              :columns 2
                              :width 32
                              :height 32
                              :cell 0
                              :filter (str "hue-rotate(" 340 "deg) "
                                           "saturate(" 0.9 ")")
                              :scale 2})
               (image/create {:x (- horizontalCenter 100)
                              :y (- verticalCenter rowHeight)
                              :imageKeyword :runesheet
                              :columns 2
                              :width 32
                              :height 32
                              :cell 1
                              :filter (str "hue-rotate(" 85 "deg) "
                                           "saturate(" 0.7 ")")
                              :scale 2})
               (image/create {:x (- horizontalCenter 140)
                              :y (- verticalCenter rowHeight)
                              :imageKeyword :runesheet
                              :columns 2
                              :width 32
                              :height 32
                              :cell 2
                              :filter (str "hue-rotate(" 180 "deg) ")

                              :scale 2})
               (image/create {:x (- horizontalCenter 180)
                              :y (- verticalCenter rowHeight)
                              :imageKeyword :runesheet
                              :columns 2
                              :width 32
                              :height 32
                              :filter (str "hue-rotate(" 280 "deg) ")
                              :cell 3
                              :scale 2})
               (text/create {:x (+ 80 horizontalCenter)
                             :y (- verticalCenter 10 rowHeight)
                             :text "One of all:"})
               (text/create {:x (+ 95 horizontalCenter)
                             :y (- verticalCenter -10 rowHeight)
                             :text "5 points and gain 1 heart"})]))))


(defn select-page [scene number]
  (set! scene.objects
        (case number
          1 (page1-init scene)
          2 (page2-init scene)
          3 (page3-init scene)
          nil)))

(def-method render-scene :instructions
  [scene context]
  (animation/draw-image context (get-in assets/images [:bg :image]) {:x 400 :y 400 :scale 1.7 :rotation 0})
  (ui/draw-card context)
  (doseq [game-obj (:objects scene)]
    (gs/render-entity game-obj context)))

(def-method update-scene :instructions
  [scene dt]
  (doseq [game-obj (:objects scene)]
    (gs/update-entity game-obj dt)))

(defn create []
  (let [objects []
        scene {:type :scene
               :id :instructions
               :page 1
               :objects objects}]
    (select-page scene 1)
    scene))
