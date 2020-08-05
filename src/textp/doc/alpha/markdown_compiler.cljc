(ns textp.doc.alpha.markdown-compiler
  (:require
    [textp.compile.alpha.core :refer [emit!] :as compile]
    [textp.compile.alpha.text :as compile-text]
    [textp.html.alpha.tags :as tags]
    [textp.html.alpha.compiler :as html-compiler]))


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
                  (compile-text/text-environment
                    (compile-seq! content))
                  href)]
    (emit! \[ content \] \( href \))))


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
  (compile-text/text-environment
    (if (sequential? x)
      (compile-seq! x)
      (compile! x))))

(comment
  (println
    (doc->md [{:type :comment
               :data "some text"}
              "\n"
              {:tag :a
               :attrs {:href "www.toto.com"}
               :content ["toto"]}]))

  (println
    (html-compiler/doc->html [{:type :comment
                               :data "some text"}
                              "\n"
                              {:tag :a
                               :attrs {:href "www.toto.com"}
                               :content ["toto"]}])))