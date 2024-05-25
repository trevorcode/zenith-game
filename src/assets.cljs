(ns assets)

(def unloaded-images {:zenith {:type :single
                               :url "assets/zenith.png"}
                      :heart {:type :single
                              :url "assets/heart.png"}
                      :bg {:type :single
                           :url "assets/background.png"}
                      :banner {:type :single
                               :url "assets/banner.png"}
                      :runesheet {:type :sheet
                                  :url "assets/runes.png"}
                      :instr1 {:type :sheet
                               :url "assets/instr1.png"}
                      :instr2 {:type :sheet
                               :url "assets/instr2.png"}})

(def unloaded-audio  {:success {:url "assets/success.wav"
                                :type :static
                                :volume 0.65}
                      :fail {:url "assets/fail.wav"
                             :type :static
                             :volume 0.6}
                      :bomp {:url "assets/bomp.wav"
                             :type :static
                             :volume 0.6}
                      :start {:url "assets/start.wav"
                              :type :static
                              :volume 0.4}
                      :ending {:url "assets/ending.wav"
                               :type :static}
                      :rockCollide1 {:url "assets/rockCollide1.wav"
                                     :type :static}
                      :rockCollide2 {:url "assets/rockCollide2.wav"
                                     :type :static}
                      :rockCollide3 {:url "assets/rockCollide3.wav"
                                     :type :static}})
