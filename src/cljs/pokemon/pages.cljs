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
     [:img.details-poke-img
      {:width 150
       :height 150
       :src (str "/images/pokemon/" (-> details :id) "-fs8.png")}]
     [:h2.details-h2 (-> pokename (split #"-") (#(map capitalize %)) (#(join " " %)))]
     [:ul.details-list
      [:li.details-tag [:span.tag-name "ID: "] [:span.tag-value (-> details :id)]]
      [:li.details-tag [:span.tag-name "Base Exp: "] [:span.tag-value (-> details :base_experience)]]
      [:li.details-tag [:span.tag-name "Height: "] [:span.tag-value (-> details :height)]]
      [:li.details-tag [:span.tag-name "Weight: "] [:span.tag-value (-> details :weight)]]]
     [:h2.details-h2 "Type"]
     [:ul.details-list
      (->> details
           :types (map
                   (comp
                    (fn [t]
                      [:a.details-tag-link
                       {:href (path-for (keyword t))
                        :key t
                        :style {:color (str "var(--" t "-f)")}}
                       [:li.details-tag
                        {:style {:backgroundColor (str "var(--" t ")")
                                 :border (str "1px solid " "var(--" t ")")}}
                        [:span.tag-value t]]])
                    (fn [t] (get-in t [:type :name])))))]
     [:h2.details-h2 "Stats"]
     [:ul.details-list
      (->> details
           :stats (map
                   (comp
                    (fn [{:keys [stat-value stat-name]}]
                      [:li.details-tag
                       {:key (str pokename "-" stat-name "-" stat-value)}
                       [:span.tag-name (str stat-name ": ")]
                       [:span.tag-value stat-value]])
                    (fn [s]
                      {:stat-value (-> s (get-in [:base_stat]))
                       :stat-name (-> s (get-in [:stat :name]) (replace #"-" " "))}))))]
     [:h2.details-h2 "Abilities"]
     [:ul.details-list
      (->> details
           :abilities (map
                       (comp
                        (fn [a]
                          [:li.details-tag
                           {:key (str pokename "-" a)}
                           [:span.tag-value a]])
                        (fn [a]
                          (-> a (get-in [:ability :name]) (replace #"-" " "))))))]
     [:h2.details-h2 "Held Items"]
     [:ul.details-list
      (->> details
           :held_items (map
                        (comp
                         (fn [i] [:li.details-tag
                                  {:key (str pokename "-" i)}
                                  [:span.tag-value i]])
                         (fn [t] (-> t (get-in [:item :name]) (replace #"-" " "))))))]
     [:h2.details-h2 "Moves"]
     [:ul.details-list
      (->> details
           :moves (map
                   (comp
                    (fn [m] [:li.details-tag
                             {:key (str pokename "-" m)}
                             [:span.tag-value m]])
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
