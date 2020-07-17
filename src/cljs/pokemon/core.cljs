(ns pokemon.core
  (:require
   [reagent.core :as reagent]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]
   [pokemon.fetches :refer [set-poke-types!]]
   [pokemon.pages :refer [page-for
                          current-page]]
   [pokemon.routes :refer [router]]
   [pokemon.util :refer [set-theme!]]))

; TODO: imagens dos pokemons
; https://medium.com/@sergio13prez/fetching-them-all-poke-api-62ca580981a2
(set-poke-types!)

(defn mount-root
  "Initialize app"
  []
  (rdom/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (clerk/initialize!)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path router path)
            current-page (:name (:data match))
            route-params (:path-params match)
            theme (-> current-page name)]
        (reagent/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params})
        (clerk/navigate-page! path)
        ((set-theme! theme))))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))
