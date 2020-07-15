(ns pokemon.components
  (:require
   [reitit.frontend :as reitit]
   [pokemon.util :refer [set-theme!
                         poketypes-keywords]]))

(def poketypes-routes
  ; TODO: definir rotas nesteadas para tipos de pokemon
  (->> poketypes-keywords (mapv (fn [k] [(str "/" (name k)) k]))))

(def all-routes
  (conj
   [["/" :index]
    ; TODO: definir rotas nesteadas para tipos de pokemon
    ["/items" ["" :items] ["/:item-id" :item]]
    ["/about" :about]]
   poketypes-routes))

(def router
  "Routes"
  (reitit/router all-routes))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

(defn poke-store-type
  [poketype]
  [:a.poketype-link
   {:style {:background-color (str "var(--" poketype ")")}
    :href (path-for (-> poketype keyword))
    :key poketype
    :on-click (set-theme! poketype)}
   poketype])
