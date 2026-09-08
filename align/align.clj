#!/usr/bin/env bb
;; tryna see if I can do better than align.pl as far as clarity and tersity go
;; attempt 1: clojure
(ns fuck
  ;; (:use [clojure.repl])
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defn id [datum] datum)

(def lines
  (if (empty? *command-line-args*)
    (doall (line-seq (java.io.BufferedReader. *in*)))
    (reduce concat (for [filename *command-line-args*]
                     (with-open [r (io/reader filename)]
                       (doall (line-seq r)))))))

;; for testing (repl fuckery, where you can't rely on argv) use this instead
;; (def lines (with-open [f (io/reader "file")] (slurp-lines f)))

(defn pad [s target-length datum]
  (concat s (repeat (max 0 (- target-length (count s))) datum)))

(defn pad-align-lines [lines]
  (let* [split-lines (mapv #(clojure.string/split % #"[,\s]+") lines)
         most-fields-in-line (reduce max (map count split-lines))
         max-field-widths (reduce #(mapv max %1 %2)
                                   (mapv #(pad % most-fields-in-line 0)
                                          (mapv #(mapv count %) split-lines)))
         padded-fields (fn [line] (map #(str/join "" (pad %1 %2 " "))
                                       line max-field-widths))]
    (map #(str/join " " (padded-fields %))  split-lines)))

(doall (for [x (pad-align-lines lines)] (println x)))
