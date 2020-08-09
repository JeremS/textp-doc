(ns fr.jeremyschoffen.textp.alpha.doc.core-test
  (:require
    [clojure.test :refer [deftest is are]]
    [fr.jeremyschoffen.textp.alpha.doc.core :as doc]
    [fr.jeremyschoffen.textp.alpha.doc.markdown-compiler :as compiler]))


(def home (doc/make-link "www.home.com"))
(def home2 (doc/make-link "www.home2.com" "home2"))


(deftest name-links
  (are [x y] (= x y)
    (home)
    {:tag :a, :attrs {:href "www.home.com"}, :content ["www.home.com"]}

    (home2)
    {:tag :a, :attrs {:href "www.home2.com"}, :content ["home2"]}

    (home {:tag :tag-args-txt
           :content ["home"]})
    {:tag :a, :attrs {:href "www.home.com"}, :content ["home"]}))

