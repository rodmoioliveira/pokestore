(ns pokemon.components
  (:require
   [reitit.frontend :as reitit]
   [pokemon.store :refer [store]]
   [pokemon.dom :as pokedom]))

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
    :on-click (fn []
                (swap! store merge {:select-store poketype})
                (-> pokedom/dom :body
                    (.setAttribute (-> pokedom/data-attr :theme) poketype)))}
   poketype])
