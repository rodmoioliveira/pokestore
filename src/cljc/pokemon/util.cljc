(ns pokemon.util
  #?(:cljs
     (:require
      [pokemon.store :refer [store]]
      [pokemon.dom :as pokedom])))

(def poke-url "https://pokeapi.co/api/v2/")
(def poke-url-type "type/")

#?(:cljs
   (defn fetch-then
     [url fns]
     (-> js/window
         (.fetch url)
         (.then #(.json %))
         (.then (fn [data] (-> data
                               (js->clj :keywordize-keys true)
                               ((fn [v] (doseq [f fns] (f v))))))))))

#?(:cljs
   (defn set-theme!
     [theme]
     (fn []
       (swap! store merge {:select-store theme})
       (-> pokedom/dom :body (.setAttribute (-> pokedom/data-attr :theme) theme)))))
