(ns pokemon.pages
  (:require
   [reagent.session :as session]

   [pokemon.store :refer [store]]
   [pokemon.util :refer [poketypes-keywords
                         get-store-pokemon
                         get-cart-total
                         get-cart-pokemon]]
   [pokemon.components :refer [poke-store-type
                               poke-nav
                               poke-list
                               nav
                               footer]]))

(defn home-page
  "TODO: escrever documentação"
  []
  [:section.poketype.padding-nav
   [:ul.poketype-list
    (->> @store :types (map poke-store-type))]])

(defn poke-store-page
  "TODO: escrever documentação"
  []
  (let [{:keys [select-store sorting search pokemon-hash]} @store
        display-pokemons (get-store-pokemon
                          pokemon-hash select-store sorting search)
        pokemons-count (count display-pokemons)
        fail-search? (zero? pokemons-count)]
    [:section.poke.padding-nav
     [poke-nav select-store display-pokemons]
     [poke-list fail-search? display-pokemons select-store]]))

(defn cart-page
  "TODO: escrever documentação"
  []
  (let [{:keys [select-store sorting search cart]} @store
        cart-pokemons (get-cart-pokemon cart sorting search)
        total (get-cart-total cart)
        pokemons-count (count cart-pokemons)
        fail-search? (zero? pokemons-count)]
    [:section.poke.padding-nav
     [poke-nav select-store cart-pokemons total]
     [poke-list fail-search? cart-pokemons select-store]]))

(defn details-page
  "TODO: escrever documentação"
  []
  (let [routing-data (session/get :route)
        pokename (get-in routing-data [:route-params :id])]
    (-> @store (get-in [:pokemon-details (-> pokename keyword)]) clj->js js/console.log)
    [:section.details.padding-nav
     [:h1 "details"]
     [:h1 "details"]
     [:h1 "details"]
     [:h1 "details"]
     [:h1 pokename]
     [:h1 "details"]]))

(defn current-page
  "Page mounting component"
  []
  (let [page (:current-page (session/get :route))]
    [:main.main
     [nav]
     [page]
     [footer]]))

(defn page-for
  "TODO: escrever documentação"
  [route]
  (cond
    (some #(= route %) [:index]) home-page
    (some #(= route %) [:cart]) cart-page
    (some #(= route %) [:details]) details-page
    (some #(= route %) poketypes-keywords) poke-store-page))
