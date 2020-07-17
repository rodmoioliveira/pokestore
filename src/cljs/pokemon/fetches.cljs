(ns pokemon.fetches
  (:require
   [pokemon.store :refer [store]]
   [pokemon.util :refer [poke-url
                         poketypes-keywords
                         poke-url-type
                         fetch-then]]))

(defn fetch-poke-types
  []
  (fetch-then
   (str poke-url poke-url-type)
   [(fn [data] (->> data
                    :results
                    (map :name)
                    (#(swap! store merge {:types %}))))]))

(defn set-poke-types!
  []
  (->> poketypes-keywords
       (map name)
       sort
       (#(swap! store merge {:types %}))))
