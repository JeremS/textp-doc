(ns textp.doc.alpha.markdown-compiler-test
  (:require
    [clojure.test :refer [deftest is]]
    [textp.doc.alpha.core :as doc]
    [textp.doc.alpha.markdown-compiler :as compiler]))


(def home (doc/make-link "www.home.com" "home"))


(deftest simple
  (is (= (compiler/doc->md [(home)
                            {:tag :div}])
         "[home](www.home.com)<div></div>")))