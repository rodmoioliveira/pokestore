(ns pokemon.store
  (:require
   [reagent.core :as reagent :refer [atom]]))

(defonce initial-state
  {:types {}
   :pokemon {:index true}
   :select-store nil})
(defonce store (atom initial-state))
