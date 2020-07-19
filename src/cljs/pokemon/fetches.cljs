(ns pokemon.fetches
  (:require
   [clojure.string :refer [split]]

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

(defn fetch-pokemon
  [poketype]
  (when-not
   (-> @store (get-in [:pokemon (-> poketype keyword)]))
    (fetch-then
     (str
      poke-url
      poke-url-type
      poketype)
     [(fn [res]
        (->> res
             :pokemon
             (mapv :pokemon)
             (mapv (fn [p] (merge p {:id
                                     (-> (split (-> p :url) #"/")
                                         last
                                         int)})))
             (remove (fn [{:keys [id]}] (or
                                         (> id 9999)
                                         (some #{id} (-> @store :unavailable-pokemon)))))
             (#(swap! store update-in [:pokemon] assoc (-> poketype keyword) %))))])))

(defn set-poke-types!
  []
  (->> poketypes-keywords
       (map name)
       sort
       (#(swap! store merge {:types %}))))
