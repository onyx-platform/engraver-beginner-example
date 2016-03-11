(ns engraver-beginner-example.sample-job-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.core.async :refer [chan sliding-buffer >!!]]
            [engraver-beginner-example.configuration :as c]
            [engraver-beginner-example.sample-job :as j]
            [engraver-beginner-example.functions :as f]
            [schema.test]
            [onyx.plugin.core-async :refer [take-segments!]]
            [onyx.test-helper :as t]
            [onyx.api]))

(def sample-messages
  [{:message "I feel the love of those who are not physically around me."}
   {:message "I take pleasure in my own solitude."}
   {:message "I am too big a gift to this world to feel self-pity."}
   {:message "I love and approve of myself."}
   {:message "I focus on breathing and grounding myself."}
   {:message "Following my intuition and my heart keeps me safe and sound."}
   {:message "I make the right choices every time."}
   {:message "I draw from my inner strength and light."}
   {:message "I trust myself."}
   {:message "I am a unique child of this world."}])

(deftest onyx-dev-job-test
  (let [tenancy-id (java.util.UUID/randomUUID)
        env-config (c/dev-env-config tenancy-id)
        peer-config (c/dev-peer-config tenancy-id)
        n-peers 5
        chan-capacity 1000
        in-chan (chan chan-capacity)
        out-chan (chan (sliding-buffer chan-capacity))]
    (t/with-test-env [test-env [n-peers env-config peer-config]]
      (let [mode :dev
            job {:workflow j/workflow
                 :catalog (j/build-catalog :dev {})
                 :flow-conditions j/flow-conditions
                 :lifecycles (j/build-lifecycles :dev)
                 :task-scheduler :onyx.task-scheduler/balanced}]

        (doseq [m sample-messages]
          (>!! in-chan m))
        (>!! in-chan :done)

        (reset! f/input-chan in-chan)
        (reset! f/output-chan out-chan)

        (let [{:keys [job-id]} (onyx.api/submit-job peer-config job)]
          (t/feedback-exception! peer-config job-id)
          (let [actual (into #{} (take-segments! out-chan))
                expected
                [{:message "I TAKE PLEASURE IN MY OWN SOLITUDE.!"}
                 {:message "I LOVE AND APPROVE OF MYSELF.!"}
                 {:message "I MAKE THE RIGHT CHOICES EVERY TIME.!"}
                 {:message "I TRUST MYSELF.!"}
                 {:message "I AM A UNIQUE CHILD OF THIS WORLD.!"}
                 {:message "Long message: I feel the love of those who are not physically around me.!"}
                 {:message "Long message: I am too big a gift to this world to feel self-pity.!"}
                 {:message "Long message: I focus on breathing and grounding myself.!"}
                 {:message "Long message: Following my intuition and my heart keeps me safe and sound.!"}
                 {:message "Long message: I draw from my inner strength and light.!"}
                 :done]]
            (is (= actual (into #{} expected)))))))))
