(ns pokemon.routes
  (:require
   [reitit.frontend :as reitit]
   [pokemon.util :refer [poketypes-keywords]]))

(def poketypes-routes
  (->> poketypes-keywords
       (mapv
        (fn [k]
          [(str "/" (name k)) ["" k] ["/:poke-id" (-> (str (name k) "-" "poke") keyword)]]))))

(def all-routes
  (conj
   [["/" :index]
    ["/about" :about]]
   poketypes-routes))

(def router
  "Routes"
  (reitit/router all-routes))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

