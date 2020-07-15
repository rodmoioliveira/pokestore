(ns pokemon.components
  (:require
   [pokemon.routes :refer [path-for]]
   [pokemon.store :refer [store]]
   [pokemon.util :refer [set-theme! poketypes-info]]))

(defn poke-store-type
  [poketype]
  [:a.poketype-link
   {:style {:background-color (str "var(--" poketype ")")}
    :href (path-for (-> poketype keyword))
    :key poketype
    :on-click (set-theme! poketype)}
   [:img.poketype-img {:src (-> poketype keyword poketypes-info :src)}]
   [:span.poketype-name poketype]])

(defn nav
  []
  (let [store-icon-src (-> @store :select-store keyword poketypes-info :src)]
    [:nav.nav
     [:ul.nav-ul
      [:li.nav-li
       (when store-icon-src
         [:img.nav-img {:src store-icon-src}])]
      [:li.nav-li
       [:a.nav-title
        {:href (path-for :index)
         :on-click (set-theme! "index")}
        [:span "PokeStore"]]]

      [:li.nav-li
       [:input.nav-input-text {:type "text"
                               :placeholder "Um pokemon qualquer..."}]
       [:input.nav-input-btn {:type "button"
                              :value "Buscar"}]]
      [:li.nav-li
       [:img.nav-img {:src "https://cdn.iconscout.com/icon/free/png-256/pokemon-pokeball-game-go-34722.png"}]]]]))

(defn footer
  []
  [:footer.footer
   [:p "Criado por Rodolfo Mói"]])
