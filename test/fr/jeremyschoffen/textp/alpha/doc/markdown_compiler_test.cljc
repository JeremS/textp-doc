(ns fr.jeremyschoffen.textp.alpha.doc.markdown-compiler-test
  (:require
    #?(:clj [clojure.test :refer [deftest is]]
       :cljs [cljs.test :refer-macros [deftest is]])
    [fr.jeremyschoffen.textp.alpha.lib.input :as textp-in :include-macros true]
    [fr.jeremyschoffen.textp.alpha.doc.core :as doc]
    [fr.jeremyschoffen.textp.alpha.doc.markdown-compiler :as compiler]
    [fr.jeremyschoffen.textp.alpha.doc.tags :as tags]))


(def home (doc/make-link "www.home.com" "home"))


(deftest links
  (is (= (compiler/doc->md [(home)
                            {:tag :div}])
         "[home](www.home.com)<div></div>")))



(deftest blocks
  (is (= (compiler/doc->md (tags/md-block {:tag :tag-args-clj
                                           :content [:type "clojure"]}
                                          {:tag :tag-args-txt
                                           :content ["(def x 1)"]}))
         "```clojure\n(def x 1)\n```")))


#?(:clj (deftest project-coords-blocks

          (is (= (textp-in/with-input {:project/maven-coords '{fr.jeremyschoffen/textp {:mvn/version "0"}}}
                                      (compiler/doc->md (tags/project-coords)))
                 "Deps coords:\n```clojure\n#:fr.jeremyschoffen{textp #:mvn{:version \"0\"}}\n```\nLein coords:\n```clojure\n[fr.jeremyschoffen/textp \"0\"]\n```"))))
