(ns pokemon.fetches
  (:require
   [cljs.pprint :refer [char-code]]
   [clojure.string :refer [split replace]]
   [clojure.set :refer [union]]

   [pokemon.store :refer [store]]
   [pokemon.util :refer [poke-url
                         poketypes-keywords
                         hash-by-id
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
             (map-indexed (fn [index p]
                            (let [offer? (> (rand-int 101) 90)]
                              (merge p {:id
                                        (-> (split (-> p :url) #"/")
                                            last
                                            int)
                                        :popularity index
                                        :offer? offer?
                                        :discount-rate (if offer? (-> [(- 25) (- 50) (- 75)] shuffle first) 0)
                                        :type (-> poketype keyword)
                                        :price (->> p :name (map char-code) (reduce +))
                                        :name (-> p :name (replace #"-" " "))}))))
             (remove (fn [{:keys [id]}] (or
                                         (> id 9999)
                                         (some #{id} (-> @store :unavailable-pokemon)))))
             ((fn [pokemons]
                (swap! store update-in [:pokemon]
                       assoc (-> poketype keyword) (->> pokemons (map (comp keyword str :id))))
                pokemons))
             (remove (fn [{:keys [id]}] (or
                                         (some #{id} (-> @store :pokemon-ids)))))
             (mapv (fn [{:keys [discount-rate price] :as p}]
                     (merge p {:price (* (/ (- 100 (- discount-rate)) 100) price)})))
             ((fn [pokemons]
                (swap! store update-in [:pokemon-ids]
                       union (->> pokemons (map (comp keyword str :id)) set))
                (swap! store
                       assoc :pokemon-hash (merge (-> @store :pokemon-hash) (hash-by-id pokemons)))))))])))

(defn set-poke-types!
  []
  (->> poketypes-keywords
       (map name)
       sort
       (#(swap! store merge {:types %}))))
