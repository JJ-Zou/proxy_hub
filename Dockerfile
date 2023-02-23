FROM openjdk:17
EXPOSE 9999
ARG JAR_FILE=./build/libs/proxy_hub-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]