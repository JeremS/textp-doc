(ns fr.jeremyschoffen.textp.alpha.doc.markdown-compiler
  (:require
    [fr.jeremyschoffen.textp.alpha.lib.compilation :refer [emit!] :as compile]
    [fr.jeremyschoffen.textp.alpha.html.compiler :as html-compiler]
    [fr.jeremyschoffen.textp.alpha.html.tags :as tags]))


(declare compile!)


(defn compile-seq! [ss]
  (doseq [s ss]
    (compile! s)))

(defmulti emit-tag! :tag)


(defmethod emit-tag! :default [t]
  (html-compiler/emit-tag*! t))


(defmethod emit-tag! :a [{:keys [attrs content]}]
  (let [href (get attrs :href \#)
        content (if (seq content)
                  (compile/text-environment
                    (compile-seq! content))
                  href)]
    (emit! \[ content \] \( href \))))


(defmethod emit-tag! ::tags/un-escaped [node]
  (html-compiler/emit-unescaped! node))


(defmulti emit-special! :type)


(defmethod emit-special! :dtd [x]
  (html-compiler/emit-dtd! x))


(defmethod emit-special! :comment [x]
  (html-compiler/emit-comment! x))


(defn compile! [node]
  (cond
    (html-compiler/special? node) (emit-special! node)
    (html-compiler/tag? node) (emit-tag! node)
    :else (emit! (html-compiler/xml-str node))))


(defn doc->md [x]
  (compile/text-environment
    (if (sequential? x)
      (compile-seq! x)
      (compile! x))))


;;----------------------------------------------------------------------------------------------------------------------
;; Sepcific stuff
;;----------------------------------------------------------------------------------------------------------------------
(defn emit-newline! [] (emit! "\n"))


(defn emit-block! [type text]
  (emit! "```" type)
  (emit-newline!)
  (emit! text)
  (emit-newline!)
  (emit! "```"))


(defmethod emit-tag! :md-block [node]
  (let [{:keys [attrs content]} node
        type (get attrs :type "text")
        content (doc->md content)]
    (emit-block! type content)))


(defmethod emit-tag! :splice [node]
  (compile-seq! (:content node)))


(comment
  (println
    (doc->md [{:type :comment
               :data "some text"}
              "\n"
              {:tag :a
               :attrs {:href "www.toto.com"}
               :content ["toto"]}

              {:tag :splice
               :attrs {:href "www.toto.com"}
               :content ["toto"]}

              {:tag ::tags/un-escaped
               :content ["&copy;"]}]))

  (println
    (doc->md [{:tag :md-block
               :attrs {:type "clojure"}
               :content ["toto"]}]))

  (println
    (html-compiler/doc->html [{:type :comment
                               :data "some text"}
                              "\n"
                              {:tag :a
                               :attrs {:href "www.toto.com"}
                               :content ["toto"]}])))