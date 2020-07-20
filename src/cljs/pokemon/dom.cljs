(ns pokemon.dom)

(def dom
  {:body (-> js/document (.getElementById "body-container"))
   :chrome-theme (-> js/document (.getElementById "chrome-theme"))})

(def data-attr
  {:theme "data-theme"
   :content "content"})
