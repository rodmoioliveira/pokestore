(ns pokemon.components
  (:require
   [pokemon.routes :refer [path-for]]
   [pokemon.util :refer [set-theme!]]))

(defn poke-store-type
  [poketype]
  [:a.poketype-link
   {:style {:background-color (str "var(--" poketype ")")}
    :href (path-for (-> poketype keyword))
    :key poketype
    :on-click (set-theme! poketype)}
   poketype])

(defn nav
  []
  [:nav.nav
   [:ul.nav-ul
    [:li.nav-li "PokeStore"]]])

(defn footer
  []
  [:footer.footer
   [:p "Criado por Rodolfo Mói"]])
