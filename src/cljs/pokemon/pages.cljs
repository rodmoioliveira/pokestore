(ns pokemon.pages
  (:require
   [reagent.session :as session]
   [clojure.string :refer [includes?]]

   [pokemon.store :refer [store]]
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
      [:span.poke-title (if (= current-page "cart") "My" "Top")]
      [poke-store-select current-page]
      [:span.poke-title "pokemons"]]
     [:nav.poke-nav.poke-nav--sort
      [:span.poke-title "Sort by"]
      [sorting-poke-select]
      [:span.poke-count
       [:span (str "(" (count pokemons))]
       [:span.poke-results (if (some #{0 1} [(count pokemons)])
                             " result"
                             " results")]
       [:span ")"]]]]))

(defn poke-stores-page []
  (fn []
    (let [current-page (-> @store :select-store)
          sorting (-> @store :sorting)
          search-term (-> @store :search)
          pokemons (get-in @store [:pokemon (keyword current-page)])
          display-pokemons (->>
                            pokemons
                            (sort-by sorting)
                            (filter
                             (fn [p]
                               (if (= search-term "")
                                 true
                                 (includes? (p :name) search-term)))))
          pokemons-count (count display-pokemons)
          fail-search? (zero? pokemons-count)]
      [:section.poke.padding-nav
       [poke-nav current-page display-pokemons]
       [:ul.poke-list
        (if fail-search?
          [:li.poke-no-results "No results :("]
          (->>
           display-pokemons
           (map (fn [{:keys [id name] :as p}]
                  [poke-item
                   (merge
                    p
                    {:key (str current-page "-" name "-" id)
                     :poke-id id
                     :current-page current-page})]))))]])))

(defn cart-page []
  (fn []
    (let [current-page (-> @store :select-store)
          sorting (-> @store :sorting)
          search-term (-> @store :search)
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
                                (if (= search-term "")
                                  true
                                  (includes? (p :name) search-term)))))

          pokemons-count (count cart-pokemons)
          fail-search? (zero? pokemons-count)]
      [:section.poke.padding-nav
       [poke-nav current-page cart-pokemons]
       [:ul.poke-list
        (if fail-search?
          [:li.poke-no-results "No results :("]
          (->>
           cart-pokemons
           (map (fn [{:keys [id name] :as p}]
                  [poke-item
                   (merge
                    p
                    {:key (str current-page "-" name "-" id)
                     :poke-id id
                     :current-page current-page})]))))]])))

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
    (some #(= route %) poketypes-keywords) poke-stores-page))
