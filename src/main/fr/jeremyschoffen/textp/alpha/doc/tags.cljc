(ns fr.jeremyschoffen.textp.alpha.doc.tags
  (:require
    [fr.jeremyschoffen.textp.alpha.lib.tag-utils :as textp-lib :include-macros true]
    [fr.jeremyschoffen.textp.alpha.lib.input :as textp-in]
    [fr.jeremyschoffen.textp.alpha.html.tags :as tags]))


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
  (textp-lib/make-xml-tag :splice {} (vec args)))


(defn- make-maven-coord-blocks [coords]
  (if coords
    (let [sym (-> coords keys first)
          version (-> coords vals first vals first)]
      ["Deps coords:"
       (clojure-block* (pr-str coords))

       "Lein coords:"
       (clojure-block* (pr-str [sym version]))])
    []))

(defn- make-git-coord-blocks [coords]
  (if coords
    ["Git coords:"])
  [])


(defn project-coords
  "Tag used to generate a 'installation section on readmes'. Will generate md clojure blocks
  with the coordinates of the project.

  The coordinates are passed using [[fr.jeremyschoffen.textp.alpha.lib.input/*input*]]. This input is expected to be a
  map with the coordinates in *tools.deps form* (`'{symbolic/coord {:mvn/version \"x.y.z\"}}`) under the key
  `:project/coords`."
  [& _]
  (let [maven-coords (:project/maven-coords textp-in/*input*)
        git-coords (:project/git-coords textp-in/*input*)]
    (when (or maven-coords git-coords)
      (let [maven-blocks (make-maven-coord-blocks maven-coords)
            git-blocks (make-git-coord-blocks git-coords)
            blocks (into maven-blocks git-blocks)]
        (apply splice (interpose "\n" blocks))))))


(defn copyright-char [& args]
  {:tag ::tags/un-escaped
   :content ["&copy;"]})