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
                         streamline
                         purchase-stage-msg
                         poketypes-keywords
                         poketypes-info]]))

(defn poke-store-type
  "TODO: escrever documentação"
  [poketype]
  [:a.poketype-link
   {:href (path-for (-> poketype keyword))
    :key poketype
    :on-click (set-theme! poketype)}
   [:img.poketype-img
    {:height 150
     :src (-> poketype keyword poketypes-info :src)}]
   [:span.poketype-name poketype]])

(defn poke-image
  "TODO: escrever documentação"
  [id]
  [:img.poke-img
   {:width 150
    :height 150
    :src (str "images/pokemon/" id ".png")}])

(defn poke-info
  "TODO: escrever documentação"
  [p in-cart?]
  [:p.poke-info
   [:span.poke-name (p :name)]
   [:span.poke-price (str "$" (p :price))]
   (when (p :offer?)
     [:span.poke-discount (str (p :discount-rate) "%")])
   (when in-cart?
     [:img.poke-in-cart
      {:src "images/store/pokeball.png"}])])

(defn poke-add-btn
  "TODO: escrever documentação"
  [in-cart? poke-id]
  [:button.poke-add {:class (when in-cart? "poke-add--in-cart")
                     :on-click (fn []
                                 (if in-cart?
                                   (swap! store update-in [:cart] disj poke-id)
                                   (swap! store update-in [:cart] conj poke-id)))}
   (str (if in-cart? "Remove from " "Add to ") "cart")])

(defn poke-item
  "TODO: escrever documentação"
  [{:keys [poke-id] :as p}]
  (let [in-cart? (some? (some (-> @store :cart) [poke-id]))]
    [:li.poke-item
     [poke-image poke-id]
     [poke-info p in-cart?]
     [poke-add-btn in-cart? poke-id]]))

(defn nav-search-input
  "TODO: escrever documentação"
  []
  [:input.nav-input-text {:type "text"
                          :placeholder "search for a pokemon..."
                          :value (-> @store :search)
                          :on-change
                          (fn [e] (swap! store
                                         assoc :search (-> e .-target .-value lower-case)))}])

(defn search-bar
  "TODO: escrever documentação"
  []
  [:li.nav-li.nav-li--inputs
   [nav-search-input]])

(defn nav-cart
  "TODO: escrever documentação"
  [purchase-stage]
  [:li.nav-li.nav-li--pokeball
   [:small.nav-count
    {:data-stage (name purchase-stage)}
    (-> @store :cart count)]
   [:a
    {:href (path-for :cart)}
    [:img.nav-img {:src "images/store/pokeball.png"}]]])

(defn nav-title
  "TODO: escrever documentação"
  []
  [:li.nav-li.nav-li--title
   [:a.nav-a
    {:href (path-for :index)
     :on-click (set-theme! "index")}
    [:span "PokeStore"]]])

(defn nav-icon
  "TODO: escrever documentação"
  []
  (let [store-icon-src (-> @store :select-store keyword poketypes-info :src)]
    [:li.nav-li.nav-li--store-icon
     (when store-icon-src
       [:img.nav-img {:src store-icon-src}])]))

(defn poke-store-select
  "TODO: escrever documentação"
  [select-store]
  [:select.poke-select.poke-select--store
   {:name "poke-store"
    :on-change #(->> % .-target .-value (str "/") accountant/navigate!)
    :defaultValue select-store}
   (->> poketypes-keywords
        (map name)
        sort
        (#(concat % ["cart"]))
        (map (fn [p]
               [:option.poke-option {:value p :key p} (capitalize p)])))])

(defn sorting-poke-select
  "TODO: escrever documentação"
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

(defn poke-sort-nav
  "TODO: escrever documentação"
  [pokemons select-store total]
  (let [{:keys [purchase-stage]} @store]
    [:nav.poke-nav.poke-nav--sort
     [:div
      [:span.poke-title "Sort by"]
      [sorting-poke-select]
      [:span.poke-count
       [:span (str "(" (count pokemons))]
       [:span.poke-results (if (some #{0 1} [(count pokemons)])
                             " result"
                             " results")]
       [:span ")"]]]
     [:div
      [:button.poke-buy
       {:data-stage (name purchase-stage)
        :on-click streamline
        :class (when
                (and (= select-store "cart") (not (zero? total)))
                 "poke-buy--active")} (-> purchase-stage-msg purchase-stage)]]]))

(defn poke-store-nav
  "TODO: escrever documentação"
  [select-store total]
  [:nav.poke-nav.poke-nav--store
   [:div
    [:span.poke-title (if (= select-store "cart") "My" "Top")]
    [poke-store-select select-store]
    (when-not (= select-store "cart")
      [:span.poke-title "pokemons"])]
   [:div
    (when (= select-store "cart")
      [:span.poke-title.poke-total
       [:span "Total: "]
       [:span.poke-total-price (str "$" total)]])]])

(defn poke-nav
  "TODO: escrever documentação"
  []
  (fn [select-store pokemons total]
    [:div.poke-nav-wrapper
     [poke-store-nav select-store total]
     [poke-sort-nav pokemons select-store total]]))

(defn poke-list
  "TODO: escrever documentação"
  [fail-search? pokemons select-store]
  [:ul.poke-list
   (if fail-search?
     [:li.poke-no-results "No results :("]
     (->>
      pokemons
      (map (fn [{:keys [id name] :as p}]
             [poke-item
              (merge
               p
               {:key (str select-store "-" name "-" id)
                :poke-id id
                :select-store select-store})]))))])

(defn nav
  "TODO: escrever documentação"
  []
  (let [{:keys [purchase-stage select-store]} @store
        nav-active? (some #{(keyword select-store)} (conj poketypes-keywords :cart))]
    [:nav.nav
     [:ul.nav-ul
      [nav-icon]
      [nav-title]
      (when nav-active? [search-bar])
      [nav-cart purchase-stage]]]))

(defn footer
  "TODO: escrever documentação"
  []
  [:footer.footer
   [:a.who
    {:target "_blank"
     :href "https://github.com/rodmoioliveira/pokestore"}
    "Who is behind this?"]])
