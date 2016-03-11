#!/bin/sh

echo "Setting shared memory for Aeron"
mount -t tmpfs -o remount,rw,nosuid,nodev,noexec,relatime,size=256M tmpfs /dev/shm

exec java $MEDIA_DRIVER_JAVA_OPTS -cp /srv/engraver-beginner-example.jar "engraver-beginner-example.aeron_media_driver" >>/var/log/aeron.log 2>&1
