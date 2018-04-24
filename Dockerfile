FROM openjdk:8-jdk-alpine

VOLUME /tmp
ARG JAR_FILE

ARG CONFIG_LOCATION
ENV CONFIG_DIR=$CONFIG_LOCATION
ADD ${JAR_FILE} app.jar
RUN mkdir -p $CONFIG_DIR

RUN apk add --update bash libstdc++ curl zip && \
    rm -rf /var/cache/apk/*

RUN rm /bin/sh && ln -s /bin/bash /bin/sh

RUN curl -L https://bintray.com/artifact/download/groovy/maven/apache-groovy-binary-2.4.8.zip -o /tmp/groovy.zip && \
    cd /usr/local && \
    unzip /tmp/groovy.zip && \
    rm /tmp/groovy.zip && \
    ln -s /usr/local/groovy-2.4.8 groovy && \
    /usr/local/groovy/bin/groovy -v && \
    cd /usr/local/bin && \
    ln -s /usr/local/groovy/bin/groovy groovy

ADD ./docker-entrypoint.sh /docker-entrypoint.sh
RUN ["chmod", "+x", "/docker-entrypoint.sh"]

ENTRYPOINT ["/docker-entrypoint.sh"]