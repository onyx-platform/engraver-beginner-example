FROM phusion/baseimage:0.9.17
MAINTAINER Excellent Person <fill@me.in>

CMD ["/sbin/my_init"]

RUN sudo apt-get install software-properties-common \
&& add-apt-repository -y ppa:webupd8team/java \
&& apt-get update \
&& echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections \
&& apt-get install -y \
software-properties-common \
oracle-java8-installer

RUN mkdir /etc/service/onyx_peer
RUN mkdir /etc/service/aeron

ADD target/engraver-beginner-example-standalone.jar /srv/engraver-beginner-example.jar

ADD script/run_peers.sh /etc/service/onyx_peer/run
ADD script/run_aeron.sh /etc/service/aeron/run

ADD script/submit_job.sh /srv/submit_job.sh
ADD script/submit_job.sh /srv/kill_job.sh

RUN chmod +x /srv/submit_job.sh
RUN chmod +x /srv/kill_job.sh

EXPOSE 40200/tcp
EXPOSE 40200/udp

RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
