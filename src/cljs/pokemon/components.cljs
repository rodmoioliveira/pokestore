(ns pokemon.components
  (:require
   [accountant.core :as accountant]
   [clojure.string :refer [capitalize
                           lower-case
                           split]]

   [pokemon.routes :refer [path-for]]
   [pokemon.store :refer [store]]
   [pokemon.util :refer [set-theme!
                         poketypes-keywords
                         poketypes-info]]))

(defn poke-store-type
  [poketype]
  [:a.poketype-link
   {:href (path-for (-> poketype keyword))
    :key poketype
    :on-click (set-theme! poketype)}
   [:img.poketype-img {:src (-> poketype keyword poketypes-info :src)}]
   [:span.poketype-name poketype]])

(defn poke-item
  [{:keys [poke-id
           name
           price
           offer?
           discount-rate]}]
  (fn []
    (let [in-cart? (some? (some (-> @store :cart) [poke-id]))]
      [:li.poke-item
       [:img.poke-img
        {:src
         (str
          "https://raw.githubusercontent.com/rodmoioliveira/desafio-loja-pokemon/master/src/images/"
          poke-id
          ".png")}]
       [:p.poke-info
        [:span.poke-name name]
        [:span.poke-price (str "$" price)]
        (when offer?
          [:span.poke-discount (str discount-rate "%")])
        (when in-cart?
          [:img.poke-in-cart
           {:src "https://cdn.iconscout.com/icon/free/png-256/pokemon-pokeball-game-go-34722.png"}])]
       [:button.poke-add {:class (when in-cart? "poke-add--in-cart")
                          :on-click (fn []
                                      (if in-cart?
                                        (swap! store update-in [:cart] disj poke-id)
                                        (swap! store update-in [:cart] conj poke-id)))}
        (str (if in-cart? "Remove from " "Add to ") "cart")]])))

(defn nav-input-text
  []
  [:input.nav-input-text {:type "text"
                          :placeholder "search for a pokemon..."
                          :value (-> @store :search)
                          :on-change
                          (fn [e] (swap! store
                                         assoc :search (-> e .-target .-value lower-case)))}])

(defn search-bar
  []
  [:li.nav-li.nav-li--inputs
   [nav-input-text]])

(defn pokeball
  []
  [:li.nav-li.nav-li--pokeball
   [:small.nav-count (-> @store :cart count)]
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

(defn poke-store-select
  [current-page]
  [:select.poke-select.poke-select--store
   {:name "poke-store"
    :on-change #(->> % .-target .-value (str "/") accountant/navigate!)
    :defaultValue current-page}
   (->> poketypes-keywords
        (map name)
        sort
        (map (fn [p]
               [:option.poke-option {:value p :key p} (capitalize p)])))])

(defn sorting-poke-select
  []
  [:select.poke-select.poke-select--sort
   {:name "poke-sorting"
    :on-change (fn [e]
                 (->> e .-target .-value keyword
                      (#(swap! store assoc-in [:sorting] %))))
    :defaultValue (-> @store :sorting name)}
   (->> [:name :popularity :price :discount-rate]
        (map name)
        sort
        (map (fn [p]
               [:option.poke-option {:value p :key p}
                (-> p (split #"-") first capitalize)])))])

(defn nav
  []
  (let [current-pokestore (-> @store :select-store keyword)
        nav-active? (some #{current-pokestore} poketypes-keywords)]
    [:nav.nav
     [:ul.nav-ul
      [store-icon]
      [nav-title]
      (when nav-active? [search-bar])
      [pokeball]]]))

(defn footer
  []
  [:footer.footer
   [:p "Criado por Rodolfo Mói"]])
