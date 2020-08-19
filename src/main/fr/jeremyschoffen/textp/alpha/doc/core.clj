(ns fr.jeremyschoffen.textp.alpha.doc.core
  (:require
    [clojure.java.io :as io]
    [clojure.spec.alpha :as s]
    [exoscale.ex :as ex]
    [fr.jeremyschoffen.textp.alpha.doc.markdown-compiler :as compiler]
    [fr.jeremyschoffen.textp.alpha.eval.core :as eval]
    [fr.jeremyschoffen.textp.alpha.eval.env.clojure]
    [fr.jeremyschoffen.textp.alpha.lib.tag-utils :as lib]
    [fr.jeremyschoffen.textp.alpha.lib.input :refer [with-input]]
    [fr.jeremyschoffen.textp.alpha.reader.core :as reader]))

;;----------------------------------------------------------------------------------------------------------------------
;; Tag cstrs
;;----------------------------------------------------------------------------------------------------------------------
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


;;----------------------------------------------------------------------------------------------------------------------
;; Document generation
;;----------------------------------------------------------------------------------------------------------------------
(defn wrap-phase [f phase-name]
  (fn [doc]
    (let [msg "Error while making document."
          ex-data {:phase phase-name}]
      (ex/try+
        (f doc)

        (catch ::ex/incorrect {:keys [type] :as data}
          (throw (ex/ex-info msg
                             type
                             (merge ex-data data)
                             (-> data meta ::ex/exception))))
        (catch Exception e
          (throw (ex-info msg
                          ex-data
                          e)))))))


(defn slurp-resource* [p]
  (-> p
      io/resource
      slurp))


(defn eval-doc* [doc]
  (eval/eval-doc-in-temp-ns doc fr.jeremyschoffen.textp.alpha.eval.env.clojure/default))


(def slurp-resource (wrap-phase slurp-resource* :slurp-phase))
(def read-document (wrap-phase reader/read-from-string :read-phase))
(def eval-doc (wrap-phase eval-doc* :eval-phase))
(def doc->md (wrap-phase compiler/doc->md :compile-phase))


(defn make-document [doc-resource-path input]
  (with-input input
    (-> doc-resource-path
        slurp-resource
        read-document
        eval-doc
        doc->md)))


;; TODO tags for clojar-like badges
