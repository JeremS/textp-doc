(ns fr.jeremyschoffen.textp.alpha.doc.core
  (:require [clojure.spec.alpha :as s]
            [fr.jeremyschoffen.textp.alpha.lib.core :as lib]))


(defn make-link
  "Make a pre-filled link as a textp tag."
  ([href]
   (make-link href href))
  ([href text]
   (let [link {:tag :a
               :attrs {:href href}
               :content [text]}]
     (fn [& args]
       (let [text' (get-in (lib/conform-or-throw (s/cat :text (s/? ::lib/tag-txt-arg)) args) [:text :content])]

         (cond-> link
                 text' (assoc :content text')))))))




;; TODO tags for clojar-like badges