(ns assets
  (:require [engine.assets :as ea]))

(def unloaded-images {:ship {:type :single
                             :url "assets/ship.png"}
                      :greencap {:type :sheet
                                 :url "assets/greencap.png"}
                      :bg {:type :single

                           :url "assets/background.png"}
                      :banner {:type :single
                           :url "assets/banner.png"}
                      :runguy {:type :sheet
                               :url "assets/runguy.jpg"}
                      :runesheet {:type :sheet
                                  :url "assets/runes.png"}})

