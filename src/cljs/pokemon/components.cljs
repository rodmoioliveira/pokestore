(ns pokemon.components
  (:require
   [pokemon.routes :refer [path-for]]
   [pokemon.store :refer [store]]
   [pokemon.util :refer [set-theme! poketypes-info]]))

(defn poke-store-type
  [poketype]
  [:a.poketype-link
   {:href (path-for (-> poketype keyword))
    :key poketype
    :on-click (set-theme! poketype)}
   [:img.poketype-img {:src (-> poketype keyword poketypes-info :src)}]
   [:span.poketype-name poketype]])

(defn search-bar
  []
  [:li.nav-li.nav-li--inputs
   [:input.nav-input-text {:type "text"
                           :placeholder "Um pokemon qualquer..."}]
   [:input.nav-input-btn {:type "button"
                          :value "Buscar"}]])

(defn pokeball
  []
  [:li.nav-li.nav-li--pokeball
   [:img.nav-img {:src "https://cdn.iconscout.com/icon/free/png-256/pokemon-pokeball-game-go-34722.png"}]])

(defn nav-title
  []
  [:li.nav-li.nav-li--title
   [:a.nav-a
    {:href (path-for :index)
     :on-click (set-theme! "index")}
    [:span "PokeStore"]]])

(defn store-icon
  []
  (let [store-icon-src (-> @store :select-store keyword poketypes-info :src)]
    [:li.nav-li.nav-li--store-icon
     (when store-icon-src
       [:img.nav-img {:src store-icon-src}])]))

(defn nav
  []
  [:nav.nav
   [:ul.nav-ul
    [store-icon]
    [nav-title]
    [search-bar]
    [pokeball]]])

(defn footer
  []
  [:footer.footer
   [:p "Criado por Rodolfo Mói"]])
