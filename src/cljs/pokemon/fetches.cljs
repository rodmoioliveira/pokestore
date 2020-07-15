(ns pokemon.fetches
  (:require
   [pokemon.store :refer [store]]
   [pokemon.util :refer [poke-url
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
