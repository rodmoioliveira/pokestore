; TODO: https://purelyfunctional.tv/guide/reagent/
; TODO: https://laptrinhx.com/guide-to-reagent-2944856621/
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

(defn poke-image
  [id]
  [:img.poke-img
   {:src
    (str
     "https://raw.githubusercontent.com/rodmoioliveira/desafio-loja-pokemon/master/src/images/"
     id
     ".png")}])

(defn poke-info
  [p in-cart?]
  [:p.poke-info
   [:span.poke-name (p :name)]
   [:span.poke-price (str "$" (p :price))]
   (when (p :offer?)
     [:span.poke-discount (str (p :discount-rate) "%")])
   (when in-cart?
     [:img.poke-in-cart
      {:src "https://cdn.iconscout.com/icon/free/png-256/pokemon-pokeball-game-go-34722.png"}])])

(defn poke-add-btn
  [in-cart? poke-id]
  [:button.poke-add {:class (when in-cart? "poke-add--in-cart")
                     :on-click (fn []
                                 (if in-cart?
                                   (swap! store update-in [:cart] disj poke-id)
                                   (swap! store update-in [:cart] conj poke-id)))}
   (str (if in-cart? "Remove from " "Add to ") "cart")])

(defn poke-item
  [{:keys [poke-id] :as p}]
  (let [in-cart? (some? (some (-> @store :cart) [poke-id]))]
    [:li.poke-item
     [poke-image poke-id]
     [poke-info p in-cart?]
     [poke-add-btn in-cart? poke-id]]))

(defn nav-search-input
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
   [nav-search-input]])

(defn nav-cart
  []
  [:li.nav-li.nav-li--pokeball
   [:small.nav-count (-> @store :cart count)]
   [:a
    {:href (path-for :cart)}
    [:img.nav-img {:src "https://cdn.iconscout.com/icon/free/png-256/pokemon-pokeball-game-go-34722.png"}]]])

(defn nav-title
  []
  [:li.nav-li.nav-li--title
   [:a.nav-a
    {:href (path-for :index)
     :on-click (set-theme! "index")}
    [:span "PokeStore"]]])

(defn nav-icon
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
        (#(concat % ["cart"]))
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
        nav-active? (some #{current-pokestore} (conj poketypes-keywords :cart))]
    [:nav.nav
     [:ul.nav-ul
      [nav-icon]
      [nav-title]
      (when nav-active? [search-bar])
      [nav-cart]]]))

(defn footer
  []
  [:footer.footer
   [:p "Criado por Rodolfo Mói"]])
