FROM java:8-jdk
MAINTAINER Excellent Person <fill@me.in>

CMD ["/sbin/my_init"]

RUN mkdir /etc/service/onyx_peer
RUN mkdir /etc/service/aeron

ADD target/engraver-beginner-example-standalone.jar /srv/engraver-beginner-example.jar

ADD script/run_peers.sh /etc/service/onyx_peer/run
ADD script/run_aeron.sh /etc/service/aeron/run

EXPOSE 40200/tcp
EXPOSE 40200/udp
