(ns pokemon.pages
  (:require
   [reagent.session :as session]

   [pokemon.store :refer [store]]
   [pokemon.util :refer [poketypes-keywords
                         get-store-pokemon
                         get-cart-pokemon]]
   [pokemon.components :refer [poke-store-type
                               poke-nav
                               poke-list
                               nav
                               footer]]))

(defn home-page []
  (fn []
    [:section.poketype.padding-nav
     [:h1.poketype-title "Choose your store"]
     [:ul.poketype-list
      (->> @store :types (map poke-store-type))]]))

(defn poke-store-page []
  (fn []
    (let [{:keys [select-store sorting search pokemon-hash]} @store
          display-pokemons (get-store-pokemon
                            pokemon-hash select-store sorting search)
          pokemons-count (count display-pokemons)
          fail-search? (zero? pokemons-count)]
      [:section.poke.padding-nav
       [poke-nav select-store display-pokemons]
       [poke-list fail-search? display-pokemons select-store]])))

(defn cart-page []
  (fn []
    (let [{:keys [select-store sorting search cart]} @store
          cart-pokemons (get-cart-pokemon cart sorting search)
          pokemons-count (count cart-pokemons)
          fail-search? (zero? pokemons-count)]
      [:section.poke.padding-nav
       [poke-nav select-store cart-pokemons]
       [poke-list fail-search? cart-pokemons select-store]])))

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
    (some #(= route %) [:cart]) cart-page
    (some #(= route %) poketypes-keywords) poke-store-page))
