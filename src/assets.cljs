(ns assets
  (:require [engine.assets :as ea]))

(def unloaded-images {:heart {:type :single
                              :url "assets/heart.png"}
                      :bg {:type :single
                           :url "assets/background.png"}
                      :banner {:type :single
                               :url "assets/banner.png"}
                      :runesheet {:type :sheet
                                  :url "assets/runes.png"}})

