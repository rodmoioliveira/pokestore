(ns pokemon.core
  (:require
   [clojure.string :refer [replace]]
   [reagent.core :as reagent]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]
   [pokemon.store :refer [store]]
   [pokemon.pages :refer [page-for
                          current-page]]
   [pokemon.components :refer [router]]
   [pokemon.util :refer [poke-url
                         set-theme!
                         poke-url-type
                         fetch-then]]))

; TODO: imagens dos pokemons
; https://medium.com/@sergio13prez/fetching-them-all-poke-api-62ca580981a2
(fetch-then
 (str poke-url poke-url-type)
 [(fn [data] (->> data
                  :results
                  (map :name)
                  (#(swap! store merge {:types %}))))])

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
            theme (-> match
                      :path
                      (replace #"/" "")
                      (#(if (= % "") "home" %)))]
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
