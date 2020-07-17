(ns pokemon.pages
  (:require
   [reagent.session :as session]
   [pokemon.store :refer [store]]
   [pokemon.routes :refer [path-for]]
   [pokemon.util :refer [poketypes-keywords]]
   [pokemon.components :refer [poke-store-type
                               nav
                               footer]]))

(defn home-page []
  (fn []
    [:section.poketype.padding-nav
     [:h1.poketype-title "Escolha sua loja de pokemon"]
     [:ul.poketype-list
      (->> @store :types (map poke-store-type))]]))

(defn poketype-list-page []
  (fn []
    (let [current-page (-> @store :select-store)]
      [:section.padding-nav
       [:h1 "The items of pokemon"]
       [:ul.item-list (map (fn [poke-id]
                             [:li.item {:name (str current-page "-" poke-id)
                                        :key (str current-page "-" poke-id)}
                              [:a {:href (path-for
                                          (-> (str current-page "-" "poke") keyword)
                                          {:poke-id poke-id})}
                               "Item: " poke-id]])
                           (range 1 60))]])))

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
