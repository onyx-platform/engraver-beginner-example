(ns engraver-beginner-example.sample-job
  (:require [engraver-beginner-example.configuration :as c]
            [onyx.plugin.kafka]
            [onyx.api])
  (:gen-class))

(def workflow
  [[:read-input :punctuate-message]
   [:punctuate-message :capitalize-short-messages]
   [:punctuate-message :prefix-long-messages]
   [:capitalize-short-messages :write-output]
   [:prefix-long-messages :write-output]])

(def batch-size 20)

(def core-async-input-task
  {:onyx/name :read-input
   :onyx/plugin :onyx.plugin.core-async/input
   :onyx/type :input
   :onyx/medium :core.async
   :onyx/batch-size batch-size
   :onyx/max-peers 1
   :onyx/doc "Reads segments from a core.async channel"})

(def core-async-output-task
  {:onyx/name :write-output
   :onyx/plugin :onyx.plugin.core-async/output
   :onyx/type :output
   :onyx/medium :core.async
   :onyx/batch-size batch-size
   :onyx/max-peers 1
   :onyx/doc "Writes segments to a core.async channel"})

(defn kafka-input [{:keys [topic zk-addr]}]
  {:onyx/name :read-messages
   :onyx/plugin :onyx.plugin.kafka/read-messages
   :onyx/type :input
   :onyx/medium :kafka
   :kafka/topic topic
   :kafka/group-id "onyx-consumer"
   :kafka/zookeeper zk-addr
   :kafka/offset-reset :smallest
   :kafka/force-reset? false
   :onyx/n-peers 1
   :onyx/batch-size batch-size
   :onyx/doc "Reads messages from a Kafka topic"})

(defn kafka-output [{:keys [topic zk-addr]}]
  {:onyx/name :write-messages
   :onyx/plugin :onyx.plugin.kafka/write-messages
   :onyx/type :output
   :onyx/medium :kafka
   :kafka/topic topic
   :kafka/zookeeper zk-addr
   :onyx/batch-size batch-size
   :onyx/doc "Writes messages to a Kafka topic"})

(defn build-catalog [mode opts]
  [(if (= mode :dev)
     core-async-input-task
     (kafka-input (:read-messages opts)))

   {:onyx/name :punctuate-message
    :onyx/fn :engraver-beginner-example.functions/punctuate-message
    :onyx/type :function
    :onyx/batch-size batch-size
    :onyx/doc "Adds a bang ('!') to the end of the message"}

   {:onyx/name :capitalize-short-messages
    :onyx/fn :engraver-beginner-example.functions/capitalize-message
    :onyx/type :function
    :onyx/batch-size batch-size
    :onyx/doc "Capitalizes the case of the message"}

   {:onyx/name :prefix-long-messages
    :onyx/fn :engraver-beginner-example.functions/prefix-message
    :onyx/type :function
    :sample/prefix "Long message: "
    :onyx/params [:sample/prefix]
    :onyx/batch-size batch-size
    :onyx/doc "Prepends text to the front of the message"}

   (if (= mode :dev)
     core-async-output-task
     (kafka-output (:write-message opts)))])

(def flow-conditions
  [{:flow/from :punctuate-message
    :flow/to [:capitalize-short-messages]
    :flow/short-circuit? true
    :flow/predicate [:not :engraver-beginner-example.functions/long-message?]
    :flow/doc "If it's not a long message, send it to the :capitalize-short-messages task only"}

   {:flow/from :punctuate-message
    :flow/to [:prefix-long-messages]
    :flow/short-circuit? true
    :flow/predicate :engraver-beginner-example.functions/long-message?
    :flow/doc "If it's a long message, send it to the :prefix-long-messages task only"}])

(def core-async-input-lifecycle
  [{:lifecycle/task :read-input
    :lifecycle/calls :engraver-beginner-example.functions/reader-ch-calls}

   {:lifecycle/task :read-input
    :lifecycle/calls :onyx.plugin.core-async/reader-calls}])

(def core-async-output-lifecycle
  [{:lifecycle/task :write-output
    :lifecycle/calls :engraver-beginner-example.functions/writer-ch-calls}

   {:lifecycle/task :write-output
    :lifecycle/calls :onyx.plugin.core-async/writer-calls}])

(def kafka-input-lifecycle
  [{:lifecycle/task :read-input
    :lifecycle/calls :onyx.plugin.kafka/read-messages-calls}])

(def kafka-output-lifecycle
  [{:lifecycle/task :write-output
    :lifecycle/calls :onyx.plugin.kafka/write-messages-calls}])

(defn build-lifecycles [mode]
  (concat
   (if (= mode :dev)
     core-async-input-lifecycle
     kafka-input-lifecycle)

   (if (= mode :dev)
     core-async-output-lifecycle
     kafka-output-lifecycle)))

(defn -main [& args]
  (let [peer-config (c/prod-peer-config)
        catalog-opts {:read-messages
                      {:topic "input-stream"
                       :zk-addr (:zookeeper/address peer-config)}
                      :write-messages
                      {:topic "output-stream"
                       :zk-addr (:zookeeper/address peer-config)}}
        job {:workflow workflow
             :catalog (build-catalog :prod catalog-opts)
             :flow-conditions flow-conditions
             :lifecycles (build-lifecycles :prod)
             :task-scheduler :onyx.task-scheduler/balanced}]
    (onyx.api/submit-job peer-config job)))
