(ns assets
  (:require [engine.assets :as ea]))

(def unloaded-images {:ship {:type :single
                             :url "assets/ship.png"}
                      :greencap {:type :sheet
                                 :url "assets/greencap.png"}
                      :bg {:type :single
                           :url "assets/bg.jpg"}
                      :runguy {:type :sheet
                               :url "assets/runguy.jpg"}})

