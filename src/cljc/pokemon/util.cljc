(ns pokemon.util
  #?(:cljs
     (:require
      [clojure.string :refer [replace]]
      [pokemon.store :refer [store]]
      [pokemon.dom :as pokedom])))

(def poke-url "https://pokeapi.co/api/v2/")
(def poke-url-type "type/")

#?(:cljs
   (defn fetch-then
     [url fns]
     (-> js/window
         (.fetch url)
         (.then #(.json %))
         (.then (fn [data] (-> data
                               (js->clj :keywordize-keys true)
                               ((fn [v] (doseq [f fns] (f v))))))))))

#?(:cljs
   (defn set-theme!
     [theme]
     (fn []
       (let [t (replace theme #"-poke" "")]
         (swap! store merge {:select-store t})
         (-> pokedom/dom :body (.setAttribute (-> pokedom/data-attr :theme) t))))))

(def
  poketypes-keywords
  [:normal :fighting :flying
   :poison :ground :rock
   :bug :ghost :steel
   :fire :water :grass
   :electric :psychic :ice
   :dragon :dark :fairy
   :unknown :shadow])
