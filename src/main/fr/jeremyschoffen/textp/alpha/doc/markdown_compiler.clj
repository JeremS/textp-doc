(ns fr.jeremyschoffen.textp.alpha.doc.markdown-compiler
  (:require
    [fr.jeremyschoffen.textp.alpha.lib.compilation :refer [emit!] :as compile]
    [fr.jeremyschoffen.textp.alpha.html.compiler :as html-compiler]
    [fr.jeremyschoffen.textp.alpha.html.tags :as tags]))


(def emit-tag! html-compiler/emit-tag!)

(derive ::md ::html-compiler/html)

(defn emit-newline! [] (emit! "\n"))


(defmethod html-compiler/emit-tag!  [::md :a] [{:keys [attrs content]}]
  (let [href (get attrs :href)
        content (if (seq content)
                  (html-compiler/doc->str content)
                  href)]
    (emit! \[ content \] \( href \))))


(defn emit-block! [type text]
  (emit! "```" type)
  (emit-newline!)
  (emit! text)
  (emit-newline!)
  (emit! "```"))


(defmethod html-compiler/emit-tag! [::md :md-block] [node]
  (let [{:keys [attrs content]} node
        type (get attrs :type "text")
        content (html-compiler/doc->str content)]
    (emit-block! type content)))


(defmethod html-compiler/emit-tag! [::md :splice] [node]
  (html-compiler/compile-seq! (:content node)))


(defn doc->md [doc]
  (html-compiler/with-implementation ::md
    (html-compiler/doc->str doc)))

(comment
  (println
    (doc->md [{:type :comment
               :data "some text"}
              "\n"

              {:tag :a
               :attrs {:href "www.toto.com"}
               :content ["toto"]}
              "\n"

              {:tag :a
               :attrs {}
               :content ["nothing"]}
              "\n"

              {:tag :a
               :attrs {:href "www.toto.com"}
               :content []}
              "\n"

              {:tag :splice
               :attrs {:href "www.toto.com"}
               :content ["toto"]}
              "\n"

              {:tag ::tags/un-escaped
               :content ["&copy;"]}]))

  (println
    (doc->md [{:tag :div
               :content [{:tag :md-block
                          :attrs {:type "clojure"}
                          :content ["toto"]}]}]))

  (println
    (html-compiler/doc->html [{:type :comment
                               :data "some text"}
                              "\n"
                              {:tag :a
                               :attrs {:href "www.toto.com"}
                               :content ["toto"]}])))
