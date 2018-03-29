FROM openjdk:8-jdk-alpine

VOLUME /tmp
ARG JAR_FILE

ARG CONFIG_LOCATION
ENV CONFIG_DIR=$CONFIG_LOCATION
ADD ${JAR_FILE} app.jar
RUN mkdir -p $CONFIG_DIR

ADD ./docker-entrypoint.sh /docker-entrypoint.sh
RUN ["chmod", "+x", "/docker-entrypoint.sh"]

ENTRYPOINT ["/docker-entrypoint.sh"]