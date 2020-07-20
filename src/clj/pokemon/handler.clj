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
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   [:link {:href "https://fonts.googleapis.com/css2?family=Lato:wght@300;400;700;900&display=swap"
           :rel "stylesheet"}]
   [:meta {:name "theme-color" :content "#f4f4f4" :id "chrome-theme"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container" :id "body-container"}
    mount-target
    (include-js "/js/app.js")
    ; [:script "pokemon.core.init_BANG_()"]
    ]))

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
      ["/about" {:get {:handler index-handler}}]
      ["/cards" {:get {:handler cards-handler}}]]
     poke-handlers))
   (reitit-ring/routes
    (reitit-ring/create-resource-handler {:path "/" :root "/public"})
    (reitit-ring/create-default-handler))
   {:middleware middleware}))
