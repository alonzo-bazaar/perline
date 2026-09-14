#!/usr/bin/env bb
;; c lexer in clojure
;; may try making it in perl as well or some shi
;; 
;; Author: Alonzo Bazaar
;; License: GPLv2.1
(ns clex
  (:use [clojure.repl])
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defn all? [s]
  "true if every element in sequence s is truthy"
  (reduce #(and %1 %2) s))

(defn sp? [a b]
  "is a a prefix of b or b a prefix of a? (are a or b a shared prefix?)"
  (or (empty? a) (empty? b) (all? (map = a b))))

(defn mem? [s elt]
  "is elt an element of sequence s?"
  (cond (seq? s)
        (loop [s s]
          (cond (empty? s) false
                (= (first s) elt) true
                :else (recur (rest s))))
        :else
        (some #(= (get s %) elt) (range (count s)))))

;; TODO: these four functions are probably not accurate
;; (I'm counting #define and shit like it as c tokens)
(defn id-start-char? [^Character c]
  "is c a character that may start an identifer?"
  (or (Character/isLetter c) (= c \_)))
(defn id-char? [^Character c]
  "is c a character that may belong to an identifier?"
  (or (Character/isLetterOrDigit c) (= c \_)))

(defn num-start-char? [^Character c]
  "is c a character that may start a number?"
  (Character/isDigit c))
(defn num-char? [^Character c]
  "is c a character that may belong to a number?"
  (or (Character/isDigit c) (mem? ".eEf" c)))

(defn split-first-token
  "split first token off of character sequence s
  returs the token it took off and the remainder of the sequence after that token"
  ([s] (split-first-token [] s))
  ([prev-toks s]
   ;; skip whitespace
   (let [s (drop-while Character/isWhitespace s)]
     (cond
       (empty? s) nil

       (sp? s "#")
       [(str/join (concat ["#"]
                          (take-while id-char? (drop 1 s))))
        (drop-while id-char? (drop 1 s))]

       (sp? s "//")
       (split-first-token prev-toks
                          (drop-while #(not (= % \newline)) s))

       (sp? s "/*")
       (loop [s s]
         (cond (empty? s) nil
               (sp? s "*/") (split-first-token prev-toks (drop 2 s))
               :else (recur (rest s)))) 

       ;; this one case is the only reason why prev-toks exists
       (and (= (last prev-toks) "#include") (sp? s "<"))
       [(str/join (concat (take-while #(not (= % \>)) s)
                          [">"]))
        (drop 1 (drop-while #(not (= % \>)) s))]

       (sp? s "\"")
       (loop [acc [] s (drop 1 s)]
         (cond (empty? s) nil
               (sp? s "\"") [(str/join (concat ["\""] acc ["\""]))
                             (drop 1 s)]
               (sp? s "\\\"") (recur (conj acc \\ \") (drop 2 s))
               :else (recur (conj acc (first s)) (rest s))))

       (id-start-char? (first s))
       [(str/join (take-while id-char? s)) (drop-while id-char? s)]

       (num-start-char? (first s))
       [(str/join (take-while num-char? s)) (drop-while num-char? s)]

       ;; TODO: better handling of floating point number literals
       (Character/isDigit (first s))
       [(str/join (take-while
                   #(or (Character/isDigit %) (mem? "eE.f" %))
                   s))
        (drop-while
         #(or (Character/isDigit %) (mem? "eE.f" %))
         s)]


       ;; operators must be checked in decreasing order of length
       ;; so that, for instance, ++ has precedence over +
       (some #(sp? s %)
             [">>=" "<<="])
       [(str/join (take 3 s)) (drop 3 s)]

       (some #(sp? s %)
             ["++" "--" "+=" "*=" "-=" "/=" "&=" "|=" "~="
              "&&" "||" ">=" "<=" "!=" "=="
              "->"
              ">>" "<<"])
       [(str/join (take 2 s)) (drop 2 s)]

       (some #(sp? s %)
             ["(" ")" "[" "]" "{" "}" "," "." "?" ":" ";"
              "+" "-" "*" "/" "<" ">" "=" "!"])
       [(str/join (take 1 s)) (drop 1 s)]


       :else (throw (Exception.
                     (str "FUCK YOU BALTIMORE:\n"
                          "prev: " (str/join prev-toks) "\n"
                          "left: " (str/join s) "\n")))))))

(defn split-tokens [s]
  (loop [toks [] s (lazy-seq s)]
    (let [[tok rst] (split-first-token toks s)]
      (if (and tok rst)
        (recur (conj toks tok) rst)
        toks))))

(defn tomfoolery [text]
  (let [toks (split-tokens text)]
    (doall
     (for [tok toks]
       (do (print tok)
           (dotimes [i (+ 1 (rand-int 10))] (print \space))
           (dotimes [i (rand-int 3)] (print \newline))
           (dotimes [i (+ 1 (rand-int 10))] (print \space)))))))

(if-not (empty? *command-line-args*)
  (doall (map #(tomfoolery (slurp %)) *command-line-args*))
  (tomfoolery (slurp *in*)))
