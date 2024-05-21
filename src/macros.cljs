(ns macros)


;; Kinda scuffed implementation off multimethods
;; Inspired by https://github.com/staab/janet-multimethod

(defn multimethodfn [dispatch]
  (let [multimethods (atom {})]
    (fn [& args]
      (if (= :attach-method (first args))
        multimethods
        (let [multimethods @multimethods
              method (or (get multimethods (apply dispatch args)) (:default multimethods))]
          (if method
            (apply method args)
            (throw (js/Error. (str "No method found for dispatch values: " args)))))))))

(defmacro def-multi
  [name dispatch]
  `(def ~name (macros/multimethodfn ~dispatch)))

(defmacro def-method
  [name dispatch-val args & body]
  `(swap! (~name :attach-method) assoc ~dispatch-val (fn [~@args] ~@body)))