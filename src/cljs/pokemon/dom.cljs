(ns pokemon.dom)

(def dom
  {:body (-> js/document (.getElementById "body-container"))})

(def data-attr
  {:theme "data-theme"})
