#!/bin/sh

echo "Starting the Aeron Media Driver"

exec java $MEDIA_DRIVER_JAVA_OPTS -cp /srv/engraver-beginner-example.jar "engraver_beginner_example.aeron_media_driver"
