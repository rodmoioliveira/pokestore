(ns pokemon.pages
  (:require
   [reagent.session :as session]

   [pokemon.store :refer [store]]
   [pokemon.routes :refer [path-for]]
   [pokemon.util :refer [poketypes-keywords]]
   [pokemon.components :refer [poke-store-type
                               poke-item
                               poke-store-select
                               sorting-poke-select
                               nav
                               footer]]))

(defn home-page []
  (fn []
    [:section.poketype.padding-nav
     [:h1.poketype-title "Choose your store"]
     [:ul.poketype-list
      (->> @store :types (map poke-store-type))]]))

(defn poke-nav
  []
  (fn [current-page pokemons]
    [:div.poke-nav-wrapper
     [:nav.poke-nav.poke-nav--store
      [:span.poke-title "Top"]
      [poke-store-select current-page]
      [:span.poke-title "pokemons"]]
     [:nav.poke-nav.poke-nav--sort
      [:span.poke-title "Sorting by"]
      [sorting-poke-select]
      [:span.poke-count
       [:span (str "(" (count pokemons))]
       [:span.poke-results " results"]
       [:span ")"]]]]))

(defn poketype-list-page []
  (fn []
    (let [current-page (-> @store :select-store)
          sorting (-> @store :sorting)
          pokemons (get-in @store [:pokemon (keyword current-page)])]
      [:section.poke.padding-nav
       [poke-nav current-page pokemons]
       [:ul.poke-list (->>
                       pokemons
                       (sort-by sorting)
                       ; FIXME: http://timothypratley.blogspot.com/2017/01/reagent-deep-dive-part-3-sequences.html
                       ; Warning: Reactive deref not supported in lazy seq, it should be wrapped in doall
                       (map (fn [{:keys [id name price]}]
                              [poke-item
                               {:name name
                                :key (str current-page "-" name "-" id)
                                :poke-id id
                                :price price
                                :current-page current-page}])))]])))

(defn pokemon-page []
  (fn []
    (let [routing-data (session/get :route)
          item (get-in routing-data [:route-params :poke-id])]
      [:section.padding-nav
       [:h1 (str "Item " item " of pokemon")]
       [:p [:a {:href (path-for :index)} "Back to the list of items"]]])))

(defn about-page []
  (fn [] [:section.padding-nav
          [:h1 "About pokemon"]]))

(defn current-page
  "Page mounting component"
  []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:main.main
       [nav]
       [page]
       [footer]])))

(defn page-for [route]
  (cond
    (some #(= route %) [:index]) home-page
    (some #(= route %) [:about]) about-page
    (some #(= route %) poketypes-keywords) poketype-list-page
    (some #(= route %) (->> poketypes-keywords
                            (map name)
                            (map #(str % "-poke"))
                            (map keyword))) pokemon-page))
