(ns engraver-beginner-example.functions)

(defn punctuate-message [segment]
  (update-in segment [:message] (fn [x] (str x "!"))))

(defn capitalize-message [segment]
  (update-in segment [:message] clojure.string/upper-case))

(defn prefix-message [prefix segment]
  (update-in segment [:message] (fn [x] (str prefix x))))

(defn long-message? [event before after all-after]
  (>= (count (:message before)) 40))

(def input-chan (atom nil))

(def output-chan (atom nil))

(defn inject-input-ch [event lifecycle]
  {:core.async/chan @input-chan})

(defn inject-output-ch [event lifecycle]
  {:core.async/chan @output-chan})

(def reader-ch-calls
  {:lifecycle/before-task-start inject-input-ch})

(def writer-ch-calls
  {:lifecycle/before-task-start inject-output-ch})
