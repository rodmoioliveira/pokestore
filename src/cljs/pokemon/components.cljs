(ns pokemon.components
  (:require
   [reitit.frontend :as reitit]
   [pokemon.util :refer [set-theme!]]))

(def router
  "Routes"
  (reitit/router
   [["/" :index]
    ["/items"
     ["" :items]
     ["/:item-id" :item]]
    ["/about" :about]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

(defn poke-store-type
  [poketype]
  [:a.poketype-link
   {:style {:background-color (str "var(--" poketype ")")}
    :href (path-for :items)
    :key poketype
    :on-click (set-theme! poketype)}
   poketype])
