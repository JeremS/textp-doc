(ns fr.jeremyschoffen.textp.alpha.doc.tags
  (:require
    [fr.jeremyschoffen.textp.alpha.lib.core :as textp-lib :include-macros true]
    [fr.jeremyschoffen.textp.alpha.lib.input :as textp-in]))


(textp-lib/def-xml-tag md-block
  "Tag representing a markdwown code block.

  ex:
  ```text
  ◊md-block[:type \"clojure\"] {
    (def plus +)
  }
  ```")


(defn- clojure-block* [text]
  (textp-lib/make-xml-tag :md-block
                          {:type "clojure"}
                          [text]))


(defn- splice [& args]
  (textp-lib/make-xml-tag :splice nil (vec args)))


(defn project-coords [& _]
  (when-let [coords (:project/coords textp-in/*input*)]
    (let [sym (-> coords keys first)
          version (-> coords vals first vals first)]
      (apply splice (interpose "\n"
                               ["Deps coords:"
                                (clojure-block* (pr-str coords))

                                "Lein coords:"
                                (clojure-block* (pr-str [sym version]))])))))




