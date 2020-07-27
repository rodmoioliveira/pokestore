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
                         get-cart-total
                         get-cart-pokemon]]
   [pokemon.components :refer [poke-store-type
                               poke-nav
                               poke-list
                               h2-details
                               span-tag-name
                               span-tag-value
                               li-details-tag
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
        details (-> @store (get-in [:pokemon-details (-> pokename keyword)]))
        in-cart? (some? (some (-> @store :cart) [(-> details :id)]))]
    [:section.details
     [:img.details-poke-img
      {:width 150
       :height 150
       :src (str "/images/pokemon/" (-> details :id) "-fs8.png")}]
     [h2-details "Info"]
     [:ul.details-list
      [li-details-tag
       {}
       [span-tag-name "Name: "]
       [span-tag-value (-> pokename (split #"-") (#(map capitalize %)) (#(join " " %)))]]
      [li-details-tag {} [span-tag-name "ID: "] [span-tag-value (-> details :id)]]
      [li-details-tag {} [span-tag-name "Base Exp: "] [span-tag-value (-> details :base_experience)]]
      [li-details-tag
       {}
       [span-tag-name "Height: "]
       [span-tag-value (str (-> details :height (/ 10)) "m")]]
      [li-details-tag
       {}
       [span-tag-name "Weight: "]
       [span-tag-value (str (-> details :weight (/ 10)) "kg")]]]
     [h2-details "Price"]
     [:ul.details-list
      [li-details-tag
       {:class "details-tag--price"}
       [span-tag-value (-> @store
                           (get-in [:pokemon-hash (-> details :id str keyword) :price])
                           (#(str "$" %)))]]
      [li-details-tag
       {:class "details-tag--discount"}
       [span-tag-value (-> @store
                           (get-in [:pokemon-hash (-> details :id str keyword) :discount-rate])
                           (#(str % "%")))]]]
     [h2-details "In Cart?"]
     [:ul.details-list
      [:li
       [:button.tag-btn
        {:on-click (fn []
                     (if in-cart?
                       (swap! store update-in [:cart] disj (-> details :id))
                       (swap! store update-in [:cart] conj (-> details :id))))
         :style {:backgroundColor (str "var(--" in-cart? ")")
                 :border (str "1px solid var(--" in-cart? ")")
                 :color (str "var(--" in-cart? "-f)")}}
        (str in-cart?)]]]
     [h2-details "Type"]
     [:ul.details-list
      (->> details
           :types (map
                   (comp
                    (fn [t]
                      [:a.details-tag-link
                       {:href (path-for (keyword t))
                        :key t
                        :style {:color (str "var(--" t "-f)")}}
                       [li-details-tag
                        {:style {:backgroundColor (str "var(--" t ")")
                                 :border (str "1px solid " "var(--" t ")")}}
                        [span-tag-value t]]])
                    (fn [t] (get-in t [:type :name])))))]
     [h2-details "Stats"]
     [:ul.details-list
      (->> details
           :stats (map
                   (comp
                    (fn [{:keys [stat-value stat-name]}]
                      [li-details-tag
                       {:key (str pokename "-" stat-name "-" stat-value)}
                       [span-tag-name (str stat-name ": ")]
                       [span-tag-value stat-value]])
                    (fn [s]
                      {:stat-value (-> s (get-in [:base_stat]))
                       :stat-name (-> s (get-in [:stat :name]) (replace #"-" " "))}))))]
     [h2-details "Abilities"]
     [:ul.details-list
      (->> details
           :abilities (map
                       (comp
                        (fn [a]
                          [li-details-tag
                           {:key (str pokename "-" a)}
                           [span-tag-value a]])
                        (fn [a] (-> a (get-in [:ability :name]) (replace #"-" " "))))))]
     [h2-details "Moves"]
     [:ul.details-list
      (->> details
           :moves (map
                   (comp
                    (fn [m] [li-details-tag
                             {:key (str pokename "-" m)}
                             [span-tag-value m]])
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
