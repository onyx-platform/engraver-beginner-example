#!/bin/sh

set -o errexit
set -o nounset
set -o xtrace

exec java $PEER_JAVA_OPTS -cp /srv/engraver-beginner-example.jar engraver_beginner_example.invoke_api kill-job $1
