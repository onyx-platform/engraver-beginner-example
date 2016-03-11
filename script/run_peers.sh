#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o xtrace

exec java $PEER_JAVA_OPTS -cp /srv/engraver-beginner-example.jar "engraver-beginner-example.launch-prod-peers" $NPEERS >>/var/log/onyx.log 2>&1
