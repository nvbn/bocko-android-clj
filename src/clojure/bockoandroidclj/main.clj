(ns bockoandroidclj.main
  (:require [neko.activity :refer [defactivity set-content-view!]]
            [neko.threading :refer [on-ui]]
            [bockoandroidclj.view :refer [get-view]]
            [bockoandroidclj.example :refer [colors]]))

(defactivity bockoandroidclj.BockoActivity
  :key :main
  :on-create
  (fn [this bundle]
    (on-ui
      (set-content-view! this (get-view this))
      (colors))))
