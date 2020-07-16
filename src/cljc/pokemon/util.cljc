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
   :dragon :dark :fairy])

#?(:cljs
   (def
     poketypes-info
     {:index {:src "https://icons-for-free.com/iconfiles/png/512/game+go+play+pokemon+icon-1320186971163542651.png"}
      :normal {:src "https://vignette.wikia.nocookie.net/pokemongo/images/f/fb/Normal.png"}
      :fighting {:src "https://vignette.wikia.nocookie.net/pokemongo/images/3/30/Fighting.png"}
      :flying {:src "https://vignette.wikia.nocookie.net/pokemongo/images/7/7f/Flying.png"}
      :poison {:src "https://vignette.wikia.nocookie.net/pokemongo/images/0/05/Poison.png"}
      :ground {:src "https://vignette.wikia.nocookie.net/pokemongo/images/8/8f/Ground.png"}
      :rock {:src "https://vignette.wikia.nocookie.net/pokemongo/images/0/0b/Rock.png"}
      :bug {:src "https://vignette.wikia.nocookie.net/pokemongo/images/7/7d/Bug.png"}
      :ghost {:src "https://vignette.wikia.nocookie.net/pokemongo/images/a/ab/Ghost.png"}
      :steel {:src "https://vignette.wikia.nocookie.net/pokemongo/images/c/c9/Steel.png"}
      :fire {:src "https://vignette.wikia.nocookie.net/pokemongo/images/3/30/Fire.png"}
      :water {:src "https://vignette.wikia.nocookie.net/pokemongo/images/9/9d/Water.png"}
      :grass {:src "https://vignette.wikia.nocookie.net/pokemongo/images/c/c5/Grass.png"}
      :electric {:src "https://vignette.wikia.nocookie.net/pokemongo/images/2/2f/Electric.png"}
      :psychic {:src "https://vignette.wikia.nocookie.net/pokemongo/images/2/21/Psychic.png"}
      :ice {:src "https://vignette.wikia.nocookie.net/pokemongo/images/7/77/Ice.png"}
      :dragon {:src "https://vignette.wikia.nocookie.net/pokemongo/images/c/c7/Dragon.png"}
      :dark {:src "https://vignette.wikia.nocookie.net/pokemongo/images/0/0e/Dark.png"}
      :fairy {:src "https://vignette.wikia.nocookie.net/pokemongo/images/4/43/Fairy.png"}}))
