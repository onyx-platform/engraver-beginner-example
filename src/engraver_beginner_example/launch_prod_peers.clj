(ns engraver-beginner-example.launch-prod-peers
  (:require [engraver-beginner-example.sample-job]
            [engraver-beginner-example.functions]
            [engraver-beginner-example.configuration :as c]
            [onyx.plugin.kafka])
  (:gen-class))

(defn standard-out-logger
  "Logger to output on std-out, for use with docker-compose"
  [data]
  (let [{:keys [output-fn]} data]
    (println (output-fn data))))

(defn -main [n & args]
  (let [n-peers (Integer/parseInt n)
        peer-config (c/prod-peer-config)
        peer-group (onyx.api/start-peer-group peer-config)
        peers (onyx.api/start-peers n-peers peer-group)]
    (println "Attempting to connect to Zookeeper @" (:zookeeper/address peer-config))
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread.
                        (fn []
                          (doseq [v-peer peers]
                            (onyx.api/shutdown-peer v-peer))
                          (onyx.api/shutdown-peer-group peer-group)
                          (shutdown-agents))))
    (println "Started peers. Blocking forever.")
    ;; Block forever.
    @(promise)))
