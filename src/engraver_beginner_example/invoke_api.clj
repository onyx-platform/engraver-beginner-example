(ns engraver-beginner-example.invoke-api
  (:require [engraver-beginner-example.sample-job :as s]
            [engraver-beginner-example.configuration :as c])
  (:gen-class))

(defn -main [command job & args]
  (cond (= command "submit-job")
        (cond (= job "sample")
              (s/-main)

              :else
              (println "Unknown job " job))

        (= command "kill-job")
        (let [peer-config (c/prod-peer-config)]
          (onyx.api/kill-job peer-config job))

        :else
        (println "Unknown command " command)))
