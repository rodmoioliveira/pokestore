; TODO: abstrair funcions de swap!
(ns pokemon.fetches
  (:require
   [clojure.string :refer [split replace]]
   [clojure.set :refer [union]]

   [pokemon.store :refer [store]]
   [pokemon.util :refer [poke-url
                         poke-url-pokemon
                         poketypes-keywords
                         create-offer
                         hash-by-id
                         poke-url-type
                         fetch-async]]))

(defn fetch-details
  "TODO: escrever documentação"
  [pokename]
  (when-not (-> @store (get-in [:pokemon-details (-> pokename keyword)]))
    (fetch-async
     (str
      poke-url
      poke-url-pokemon
      (replace pokename #" " "-"))
     [(fn [res]
        (->> res
             ((fn [poke-details]
                (let [{:keys [id]} poke-details
                      in-hash? (some? (get-in @store [:pokemon-hash (-> id str keyword)]))
                      poke-offer (create-offer id poke-details)]
                  (when-not in-hash?
                    (swap! store
                           assoc :pokemon-hash (merge (-> @store :pokemon-hash) (hash-by-id [poke-offer]))))
                  (swap! store update-in [:pokemon-details]
                         assoc (-> pokename keyword) poke-details))))))])))

(defn fetch-store
  "TODO: escrever documentação"
  [poketype]
  (when-not (-> @store (get-in [:pokemon (-> poketype keyword)]))
    (fetch-async
     (str
      poke-url
      poke-url-type
      poketype)
     [(fn [res]
        (->> res
             :pokemon
             (mapv (comp
                    (fn [p]
                      (let [id (-> (split (-> p :url) #"/")
                                   last
                                   int)]
                        (merge p (create-offer id p))))
                    :pokemon))
             (remove (fn [{:keys [id]}] (or
                                         (> id 9999)
                                         (some #{id} (-> @store :unavailable-pokemon)))))
             ((fn [pokemons]
                (swap! store update-in [:pokemon]
                       assoc (-> poketype keyword) (->> pokemons (map (comp keyword str :id))))
                pokemons))
             (remove (fn [{:keys [id]}] (or
                                         (some #{id} (-> @store :pokemon-ids)))))
             ((fn [pokemons]
                (swap! store update-in [:pokemon-ids]
                       union (->> pokemons (map (comp keyword str :id)) set))
                (doseq [pokemon pokemons]
                  (let [poke-hash (hash-by-id [pokemon])
                        id (-> pokemon :id str keyword)
                        in-hash? (some? (get-in @store [:pokemon-hash id]))]
                    (when-not in-hash?
                      (swap! store
                             assoc :pokemon-hash (merge (-> @store :pokemon-hash) poke-hash)))))))))])))

(defn set-poke-types!
  "TODO: escrever documentação"
  []
  (->> poketypes-keywords
       (map name)
       sort
       (#(swap! store merge {:types %}))))
