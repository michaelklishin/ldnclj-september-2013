(ns team2.core
  (:require [clojure.java.io :as io]
            [clojure.edn     :as edn])
  (:gen-class))

(def log-path "/tmp/edn-db.log")
(def log-ostream (io/make-writer (io/as-file log-path) {:encoding "UTF-8"
                                                        :append   true}))

(defn format-event
  [host ts app s msg]
  (format "Logging %s %s %s %s %s" host ts app s msg))

(defn write-db
  [host ts app s msg]
  (binding [*out* log-ostream]
    (prn {:host host :ts ts :app app :severity s :msg msg})
    (.flush log-ostream)))

(defn echo
  [host ts app s msg]
  (println (format-event host ts app s msg)))

(defn write-cmd
  [host ts app s msg]
  (echo host ts app s msg)
  (write-db host ts app s msg))

(defn read-db
  []
  (edn/read-string (str "[" (slurp log-path) "]")))

(defn read-cmd
  []
  (println (read-db)))

(defn -main
  "CLI entry point"
  [cmd & args]
  (case cmd
    "read"  (read-cmd)
    "write" (apply write-cmd args)
    :else (println "Don't know what to do with %s")))
