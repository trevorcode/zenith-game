(ns engine.assets)

(def images {})
(def animations {})
(def audio {})

(defn register-image [kw image-url]
  (assoc! images kw image-url))

(defn register-images [image-map]
  (set! images (merge images image-map)))

(defn register-animations [animation-map]
  (set! animations (merge animations animation-map)))

(defn register-audio [audio-map]
  (set! audio (merge audio audio-map)))

(defn load-image [url]
  (-> (new js/Image)
      (assoc! :src url)))

(defn load-images []
  (->> images
       (mapv (fn [[k v]] {k (assoc v :image (load-image (:url v)))}))
       (into {})
       (set! images)))

(defn load-audio [{:keys [url type]}]
  (let [audio (-> (new js/Audio)
                  (assoc! :src url)
                  (assoc! :volume 1.0)
                  (assoc! :preload "auto")
                  (assoc! :controls false)
                  (assoc! :currentTime 0)
                  (assoc! :loop (if (= type :stream) true false)))]
    (set! audio.style.display "none")
    audio))

(defn load-audios []
  (->> audio
       (mapv (fn [[k v]] {k (assoc v :audio (load-audio v))}))
       (into {})
       (set! audio)))

(defn play-audio [audio-kw]
  (.play (.cloneNode (get-in audio [audio-kw :audio]))))