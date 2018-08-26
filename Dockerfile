FROM openjdk:10-jre

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/versionmonitor/app.jar"]

# Add service
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/versionmonitor/app.jar

EXPOSE 8080
