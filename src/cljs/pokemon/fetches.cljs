(ns pokemon.fetches
  (:require
   [cljs.pprint :refer [char-code]]
   [clojure.string :refer [split]]

   [pokemon.store :refer [store]]
   [pokemon.util :refer [poke-url
                         poketypes-keywords
                         poke-url-type
                         fetch-then]]))

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
             (map-indexed (fn [index p] (merge p {:id
                                     (-> (split (-> p :url) #"/")
                                         last
                                         int)
                                     :popularity index
                                     :price
                                     (->> p :name (map char-code) (reduce +))})))
             (remove (fn [{:keys [id]}] (or
                                         (> id 9999)
                                         (some #{id} (-> @store :unavailable-pokemon)))))
             vec
             (#(swap! store update-in [:pokemon] assoc (-> poketype keyword) %))))])))

(defn set-poke-types!
  []
  (->> poketypes-keywords
       (map name)
       sort
       (#(swap! store merge {:types %}))))
