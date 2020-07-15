(ns pokemon.util)

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
