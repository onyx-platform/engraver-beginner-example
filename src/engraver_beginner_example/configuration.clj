(ns engraver-beginner-example.configuration)

(defn dev-env-config [onyx-tenancy-id]
  {:onyx/id onyx-tenancy-id
   :onyx.bookkeeper/server? true
   :onyx.bookkeeper/delete-server-data? true
   :onyx.bookkeeper/local-quorum? true
   :onyx.bookkeeper/local-quorum-ports [3196 3197 3198]
   :zookeeper/address "127.0.0.1:2188"
   :zookeeper/server? true
   :zookeeper.server/port 2188})

(defn dev-peer-config [onyx-tenancy-id]
  {:onyx/id onyx-tenancy-id
   :zookeeper/address "127.0.0.1:2188"
   :onyx.peer/job-scheduler :onyx.job-scheduler/greedy
   :onyx.peer/zookeeper-timeout 15000
   :onyx.messaging/allow-short-circuit? true
   :onyx.messaging/impl :aeron
   :onyx.messaging/bind-addr "localhost"
   :onyx.messaging/peer-port 40200
   :onyx.messaging.aeron/embedded-driver? true})

(defn prod-peer-config []
  {:onyx.peer/job-scheduler :onyx.job-scheduler/greedy
   :onyx.peer/zookeeper-timeout 15000
   :onyx.messaging/allow-short-circuit? true
   :onyx.messaging/impl :aeron
   :onyx.messaging.aeron/embedded-driver? false
   :onyx/id (System/getenv "ONYX_ID")
   :onyx.messaging/bind-addr (System/getenv "BIND_ADDR")
   :onyx.messaging/peer-port (System/getenv "PEER_PORT")
   :zookeeper/address (System/getenv "ZK_CONN_STR")})
