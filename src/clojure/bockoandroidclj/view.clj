(ns bockoandroidclj.view
  (:require [neko.log :as log]
            [bockoandroidclj.bocko :refer [set-create-canvas clear-screen
                                                   current-color]])
  (:import (android.view SurfaceView SurfaceHolder$Callback)
           (android.graphics Canvas Paint)))

(defonce stable-timeout 10)
(defonce max-timeout 1000)
(defonce wait-timeout 10)
(defonce last-attempt (atom []))
(defonce timeouted (atom 0))

(defn draw!
  [color-map pixel-width pixel-height new {:keys [holder]}]
  (let [canvas (.lockCanvas holder nil)
        paint (Paint.)]
    (try
      (locking canvas
        (doseq [[x col] (map-indexed vector new)
                [y color] (map-indexed vector col)
                :let [left (* x pixel-width)
                      top (* y pixel-height)
                      right (+ pixel-width left)
                      bottom (+ top pixel-height)
                      [r g b] (get color-map color)]]
          (.setARGB paint 255 r g b)
          (.drawRect canvas left top right bottom paint)))
      (catch Exception e (log/e "WTF?" e))
      (finally (when canvas
                 (.unlockCanvasAndPost holder canvas))))))

(defn is-stable?
  [new]
  (when-not (= @last-attempt new)
    (reset! last-attempt new)
    (Thread/sleep stable-timeout)
    (swap! timeouted + stable-timeout)
    (when-let [stable (= @last-attempt new)]
      (reset! timeouted 0)
      stable)))

(defn wait-state
  [state]
  (loop []
    (when-not (:holder @state)
      (Thread/sleep wait-timeout)
      (recur))))

(def to-render (agent clear-screen))

(defn init-canvas!
  [state]
  (set-create-canvas
    (fn [color-map raster width _ pixel-width pixel-height]
      (add-watch raster :monitor
                 (fn [_ _ _ new] (send to-render (constantly new))))
      (add-watch to-render :monitor
                 (fn [_ _ _ _]
                   (let [new @raster]
                     (wait-state state)
                     (when (is-stable? new)
                       (let [state @state
                             new-width (/ (:width state) width)]
                         (draw! color-map
                                new-width
                                (* (/ new-width pixel-width) pixel-height)
                                new state)))))))))

(defn- make-view
  [state context]
  (proxy [SurfaceView SurfaceHolder$Callback] [context]
    (surfaceCreated [holder]
      (swap! state assoc
             :holder holder))
    (surfaceChanged [holder format width height]
      (swap! state assoc
             :width width
             :height height))
    (surfaceDestroyed [holder])))

(defn get-view
  [context]
  (let [state (atom {})
        view (make-view state context)]
    (.. view getHolder (addCallback view))
    (init-canvas! state)
    view))
