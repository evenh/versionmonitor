FROM openjdk:8-jre

COPY versionmonitor*.jar app.jar
RUN sh -c 'touch /app.jar'

VOLUME /tmp
VOLUME /var/log/versionmonitor

ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]

EXPOSE 8080
