(ns engine.input
  (:require [gamestate :as gamestate]))

(def keys {:LEFT 37
           :DOWN 40
           :UP 38
           :RIGHT 39
           :A 65
           :S 83
           :D 68
           :W 87
           :SPACE 32})

(defn key-down?
  ([keycode]
   (key-down? gamestate/game-state keycode))

  ([game-state keycode]
   (get-in game-state [:keyboard keycode])))

(defn subscribe-to-keyboard-down-events [game-state]
  (js/document.addEventListener
   "keydown"
   (fn [e]
     (when (not (get-in game-state [:keyboard (:keyCode e)]))
       (conj! (:keyboard game-state) (:keyCode e))))))

(defn subscribe-to-keyboard-up-events [game-state]
  (js/document.addEventListener
   "keyup"
   (fn [e]
     (disj! (:keyboard game-state) (:keyCode e)))))

(defn subscribe-to-keyboard-events [game-state]
  (subscribe-to-keyboard-down-events game-state)
  (subscribe-to-keyboard-up-events game-state))