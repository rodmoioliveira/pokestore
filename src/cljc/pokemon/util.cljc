(ns pokemon.util
  #?(:cljs
     (:require
      [clojure.string :refer [replace includes?]]

      [pokemon.store :refer [store]]
      [pokemon.dom :as pokedom])))

(def poke-url "https://pokeapi.co/api/v2/")
(def poke-url-type "type/")
(def pages-themes
  {:index "#f4f4f4"
   :normal "#75505B"
   :fighting "#994025"
   :flying "#49667C"
   :poison "#5E2D88"
   :ground "#A7702F"
   :rock "#48180B"
   :bug "#1C4B27"
   :ghost "#33336B"
   :steel "#61746D"
   :fire "#B01C25"
   :water "#1453E1"
   :grass "#147B3E"
   :electric "#E1E328"
   :psychic "#A42A6C"
   :ice "#86D1F3"
   :dragon "#468A99"
   :dark "#040706"
   :fairy "#971844"
   :shadow "#705898"})

#?(:cljs
   (defn fetch-then
     "TODO: escrever documentação"
     [url fns]
     (-> js/window
         (.fetch url)
         (.then #(.json %))
         (.then (fn [data] (-> data
                               (js->clj :keywordize-keys true)
                               ((fn [v] (doseq [f fns] (f v))))))))))

#?(:cljs
   (defn set-theme!
     "TODO: escrever documentação"
     [theme]
     (fn []
       (let [t (replace theme #"-poke" "")]
         (swap! store merge {:select-store t})
         (-> pokedom/dom :chrome-theme (.setAttribute (-> pokedom/data-attr :content)
                                                      (get-in pages-themes [(keyword t)])))
         (-> pokedom/dom :body (.setAttribute (-> pokedom/data-attr :theme) t))))))

(defn hash-by
  "TODO: escrever documentação"
  [key acc cur]
  (assoc acc (-> cur key str keyword) cur))

(defn hash-by-id
  "TODO: escrever documentação"
  [v]
  (reduce (partial hash-by :id) (sorted-map) v))

(def
  poketypes-keywords
  [:normal :fighting :flying
   :poison :ground :rock
   :bug :ghost :steel
   :fire :water :grass
   :electric :psychic :ice
   :dragon :dark :fairy])

#?(:cljs
   (def
     poketypes-info
     {:index {:src "images/store/star-fs8.png"}
      :cart {:src "images/store/star-fs8.png"}
      :normal {:src "images/types/Normal-fs8.png"}
      :fighting {:src "images/types/Fighting-fs8.png"}
      :flying {:src "images/types/Flying-fs8.png"}
      :poison {:src "images/types/Poison-fs8.png"}
      :ground {:src "images/types/Ground-fs8.png"}
      :rock {:src "images/types/Rock-fs8.png"}
      :bug {:src "images/types/Bug-fs8.png"}
      :ghost {:src "images/types/Ghost-fs8.png"}
      :steel {:src "images/types/Steel-fs8.png"}
      :fire {:src "images/types/Fire-fs8.png"}
      :water {:src "images/types/Water-fs8.png"}
      :grass {:src "images/types/Grass-fs8.png"}
      :electric {:src "images/types/Electric-fs8.png"}
      :psychic {:src "images/types/Psychic-fs8.png"}
      :ice {:src "images/types/Ice-fs8.png"}
      :dragon {:src "images/types/Dragon-fs8.png"}
      :dark {:src "images/types/Dark-fs8.png"}
      :fairy {:src "images/types/Fairy-fs8.png"}}))

#?(:cljs
   (defn filter-by
     "TODO: escrever documentação"
     [search]
     (fn [p]
       (if (= search "")
         true
         (includes? (p :name) search)))))

#?(:cljs
   (defn get-cart-total
     "TODO: escrever documentação"
     [cart]
     (->> cart
          vec
          (map (comp
                #(get-in @store [:pokemon-hash % :price])
                keyword
                str))
          (reduce +))))

(def sorting-fns
  {:name (juxt :name :popularity)
   :popularity (juxt :popularity :name)
   :price (juxt :price :discount-rate)
   :discount-rate (juxt :discount-rate :price)})

#?(:cljs
   (defn get-cart-pokemon
     "TODO: escrever documentação"
     [cart sorting search]
     (->> cart
          vec
          (map (comp
                #(get-in @store [:pokemon-hash %])
                keyword
                str))
          (sort-by (get sorting-fns sorting))
          (filter (filter-by search)))))

#?(:cljs
   (defn get-store-pokemon
     "TODO: escrever documentação"
     [pokemon-hash select-store sorting search]
     (let [pokemons (->>
                     (get-in @store [:pokemon (keyword select-store)])
                     (map #(get pokemon-hash %)))
           store-pokemon (->>
                          pokemons
                          (sort-by (get sorting-fns sorting))
                          (filter (filter-by search)))]
       store-pokemon)))

(def purchase-stage-msg
  {:buy "Buy now"
   :thanks "Thanks ;)"
   :shipping "Shipping..."
   :done "Done!"})

#?(:cljs
   (defn sleep
     "TODO: escrever documentação"
     [f ms]
     (js/setTimeout f ms)))

#?(:cljs
   (defn set-purchase-stage!
     "TODO: escrever documentação"
     [stage]
     (swap! store
            assoc :purchase-stage stage)))

#?(:cljs
   (defn streamline
     "TODO: escrever documentação"
     []
     (doseq [{:keys [t f]} [{:t 0
                             :f #(set-purchase-stage! :thanks)}
                            {:t 2000
                             :f #(set-purchase-stage! :shipping)}
                            {:t 4000
                             :f #(set-purchase-stage! :done)}
                            {:t 6000
                             :f #(swap! store assoc :cart #{})}
                            {:t 6500
                             :f #(set-purchase-stage! :buy)}]]
       (sleep f t))))
