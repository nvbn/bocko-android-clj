(ns bockoandroidclj.example
  (:require [neko.log :as log]
            [bockoandroidclj.bocko :as b]))

(defn colors
  []
  (doseq [[c n] (map vector
                     [:black :red :dark-blue :purple
                      :dark-green :dark-gray :medium-blue :light-blue
                      :brown :orange :light-gray :pink
                      :light-green :yellow :aqua :white]
                     (range))]
    (b/color c)
    (log/i "WTF?" c @b/current-color)
    (let [x' (* 10 (rem n 4))
          y' (* 10 (quot n 4))]
      (doseq [x (range x' (+ 10 x'))
              y (range y' (+ 10 y'))]
        (b/plot x y)))))
