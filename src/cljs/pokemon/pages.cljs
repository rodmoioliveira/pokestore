(ns pokemon.pages
  (:require
   [reagent.session :as session]
   [clojure.string :refer [includes?]]

   [pokemon.store :refer [store]]
   [pokemon.util :refer [poketypes-keywords]]
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
    (let [select-store (-> @store :select-store)
          sorting (-> @store :sorting)
          search (-> @store :search)
          pokemons (->>
                    (get-in @store [:pokemon (keyword select-store)])
                    (map #(get-in @store [:pokemon-hash %])))
          display-pokemons (->>
                            pokemons
                            (sort-by sorting)
                            (filter
                             (fn [p]
                               (if (= search "")
                                 true
                                 (includes? (p :name) search)))))
          pokemons-count (count display-pokemons)
          fail-search? (zero? pokemons-count)]
      [:section.poke.padding-nav
       [poke-nav select-store display-pokemons]
       [poke-list fail-search? display-pokemons select-store]])))

(defn cart-page []
  (fn []
    (let [select-store (-> @store :select-store)
          sorting (-> @store :sorting)
          search (-> @store :search)
          cart-pokemons (->> @store
                             :cart
                             vec
                             (map (comp
                                   #(get-in @store [:pokemon-hash %])
                                   keyword
                                   str))
                             (sort-by sorting)
                             (filter
                              (fn [p]
                                (if (= search "")
                                  true
                                  (includes? (p :name) search)))))

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
