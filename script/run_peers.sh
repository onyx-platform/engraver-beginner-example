#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o xtrace

exec java $PEER_JAVA_OPTS -cp /srv/engraver-beginner-example.jar "engraver-beginner-example.launch_prod_peers" $N_PEERS >>/var/log/onyx.log 2>&1
