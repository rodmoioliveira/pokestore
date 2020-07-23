(ns pokemon.handler
  (:require
   [reitit.ring :as reitit-ring]
   [hiccup.page :refer [include-js include-css html5]]
   [config.core :refer [env]]

   [pokemon.util :refer [poketypes-keywords]]
   [pokemon.middleware :refer [middleware]]))

(def mount-target
  [:div#app
   [:h2 "Welcome to pokemon"]
   [:p "please wait while Figwheel/shadow-cljs is waking up ..."]
   [:p "(Check the js console for hints if nothing exciting happens.)"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
   [:title "PokeStore"]
   [:meta {:content "PokeStore", :name "title"}]
   [:meta {:content "Buy a whole bunch of pokemons!", :name "description"}]
   [:meta {:content "clojure, clojurescript, reagent, pokemon-api, pokemon, pokeapi, pokedex-application, pokemons"
           :name "keywords"}]
   [:meta {:content "en-us", :name "language"}]
   [:meta {:content "rodmoi.oliveira@gmail.com", :name "reply-to"}]
   [:meta {:content "Rodolfo Mói, rodmoi.oliveira@gmail.com", :name "author"}]
   [:meta {:content "Rodolfo Mói", :name "designer"}]
   [:meta {:content "Rodolfo Mói", :name "copyright"}]
   [:meta {:content "Rodolfo Mói", :name "owner"}]
   [:meta {:content "Worldwide", :name "coverage"}]
   [:meta {:content "Global", :name "distribution"}]
   [:meta {:content "General", :name "rating"}]

   ; "<!-- Favicon -->"
   [:link {:href "favicon/apple-touch-icon.png", :sizes "180x180", :rel "apple-touch-icon"}]
   [:link {:href "favicon/favicon-32x32.png", :sizes "32x32", :type "image/png", :rel "icon"}]
   [:link {:href "favicon/favicon-16x16.png", :sizes "16x16", :type "image/png", :rel "icon"}]
   [:link {:color "#ff0000", :href "favicon/safari-pinned-tab.svg", :rel "mask-icon"}]
   [:link {:href "favicon/favicon.ico", :rel "shortcut icon"}]
   [:link {:href "manifest/manifest.json", :rel "manifest"}]
   [:meta {:content "#f4f4f4", :name "msapplication-TileColor"}]

   ; "<!-- Urls -->"
   [:meta {:content "https://aqueous-mesa-22699.herokuapp.com/", :name "url"}]
   [:meta {:content "https://aqueous-mesa-22699.herokuapp.com/", :name "identifier-URL"}]

   ; "<!-- Open Graph / Facebook -->"
   [:meta {:content "website", :property "og:type"}]
   [:meta {:content "https://aqueous-mesa-22699.herokuapp.com/", :property "og:url"}]
   [:meta {:content "PokeStore", :property "og:title"}]
   [:meta {:content "Buy a whole bunch of pokemons!", :property "og:description"}]
   [:meta {:content "images/social/social-card.jpg",
           :property "og:image"}]

   ; "<!-- Twitter -->"
   [:meta {:content "summary_large_image", :property "twitter:card"}]
   [:meta {:content "https://aqueous-mesa-22699.herokuapp.com/", :property "twitter:url"}]
   [:meta {:content "PokeStore", :property "twitter:title"}]
   [:meta {:content "Buy a whole bunch of pokemons!",
           :property "twitter:description"}]
   [:meta {:content "images/social/social-card.jpg",
           :property "twitter:image"}]

   ; "<!-- Color -->"
   [:meta {:name "theme-color" :content "#f4f4f4" :id "chrome-theme"}]

   ; "<!-- Fonts -->"
   [:link {:href "https://fonts.googleapis.com/css2?family=Lato:wght@300;400;700;900&display=swap" :rel "stylesheet"}]

   ; "<!-- Styles -->"
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container" :id "body-container"}
    mount-target
    (include-js "/js/app.js")]))

(defn cards-page []
  (html5
   (head)
   [:body
    mount-target
    (include-js "/js/app_devcards.js")]))

(defn index-handler
  [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (loading-page)})

(defn cards-handler
  [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (cards-page)})

(def poke-handlers
  (->> poketypes-keywords
       (map (fn [pokemon]
              [(str "/" (name pokemon))
               ["" {:get {:handler index-handler}}]
               ["/:poke-id" {:get
                             {:handler index-handler
                              :parameters {:path {:poke-id int?}}}}]]))))

(def app
  (reitit-ring/ring-handler
   (reitit-ring/router
    (conj
     [["/" {:get {:handler index-handler}}]
      ["/cart" {:get {:handler index-handler}}]
      ["/cards" {:get {:handler cards-handler}}]]
     poke-handlers))
   (reitit-ring/routes
    (reitit-ring/create-resource-handler {:path "/" :root "/public"})
    (reitit-ring/create-default-handler))
   {:middleware middleware}))
