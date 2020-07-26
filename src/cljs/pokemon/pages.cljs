(ns pokemon.pages
  (:require
   [clojure.string :refer [replace
                           split
                           join
                           capitalize]]
   [reagent.session :as session]

   [pokemon.routes :refer [path-for]]
   [pokemon.store :refer [store]]
   [pokemon.util :refer [poketypes-keywords
                         get-store-pokemon
                         poketypes-info
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
        pokename (get-in routing-data [:route-params :id])
        details (-> @store (get-in [:pokemon-details (-> pokename keyword)]))]
    (->> details clj->js js/console.log)
    [:section.details
     [:h2 "Name"]
     [:h2
      (-> pokename (split #"-") (#(map capitalize %)) (#(join " " %)))]
     [:h2 "Base Exp"]
     [:h2 (-> details :base_experience)]
     [:h2 "height"]
     [:h2 (-> details :height)]
     [:h2 "weight"]
     [:h2 (-> details :weight)]
     [:img.details-poke-img
      {:width 150
       :height 150
       :src (str "/images/pokemon/" (-> details :id) "-fs8.png")}]
     [:h2 "Stats"]
     [:ul.details-stats-list
      (->> details
           :stats (map
                   (comp
                    (fn [{:keys [stat-value stat-name]}]
                      [:li.details-stats-item
                       {:key (str pokename "-" stat-name "-" stat-value)}
                       (str stat-name stat-value)])
                    (fn [s]
                      {:stat-value (-> s (get-in [:base_stat]))
                       :stat-name (-> s (get-in [:stat :name]) (replace #"-" " "))}))))]
     [:h2 "Abilities"]
     [:ul.details-ability-list
      (->> details
           :abilities (map
                       (comp
                        (fn [a]
                          [:li.details-ability-item
                           {:key (str pokename "-" a)}
                           a])
                        (fn [a]
                          (-> a (get-in [:ability :name]) (replace #"-" " "))))))]
     [:h2 "Type"]
     [:ul.details-type-list
      (->> details
           :types (map
                   (comp
                    (fn [{:keys [type src]}]
                      [:li.details-type-item
                       {:key (name type)}
                       [:a
                        {:href (path-for type)}
                        [:img.details-type-img
                         {:width 150
                          :height 150
                          :src src}]]])
                    (fn [k] {:type k
                             :src (get-in poketypes-info [k :src])})
                    keyword
                    (fn [t] (get-in t [:type :name])))))]
     [:h2 "Held Items"]
     [:ul.details-held-items
      (->> details
           :held_items (map
                        (comp
                         (fn [i] [:li.details-held-item
                                  {:key (str pokename "-" i)}
                                  i])
                         (fn [t] (-> t (get-in [:item :name]) (replace #"-" " "))))))]
     [:h2 "Moves"]
     [:ul.details-moves-list
      (->> details
           :moves (map
                   (comp
                    (fn [m] [:li.details-moves-item
                             {:key (str pokename "-" m)}
                             m])
                    (fn [t] (-> t (get-in [:move :name]) (replace #"-" " "))))))]]))

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
