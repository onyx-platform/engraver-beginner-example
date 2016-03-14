(ns engraver-beginner-example.functions
  (:require [clj-kafka.admin :as admin]
            [clj-kafka.zk :refer [brokers broker-list]]))

(defn punctuate-message [segment]
  (update-in segment [:message] (fn [x] (str x "!"))))

(defn capitalize-message [segment]
  (update-in segment [:message] clojure.string/upper-case))

(defn prefix-message [prefix segment]
  (update-in segment [:message] (fn [x] (str prefix x))))

(defn long-message? [event before after all-after]
  (>= (count (:message before)) 40))

(defn deserialize-message [bytes]
  (read-string (String. bytes "UTF-8")))

(defn serialize-message [segment]
  (.getBytes (pr-str segment)))

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

(defn create-input-topic [event lifecycle]
  (let [task-map (:onyx.core/task-map event)
        topic (:kafka/topic task-map)
        zk-addr (:kafka/zookeeper task-map)
        cfg {:partitions 1
             :replication-factor 1
             :config {"cleanup.policy" "compact"}}]
    (with-open [zk (admin/zk-client zk-addr)]
      (when-not (admin/topic-exists? zk topic)
        (admin/create-topic zk topic cfg)))
    {}))

(def kafka-topic-setup
  {:lifecycle/before-task-start create-input-topic})
