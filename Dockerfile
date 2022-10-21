#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
MAINTAINER prasadyadav(prasad.karthikeyan18@gmail.com)
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean install
#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/technoidentity-api.jar /usr/local/lib/technoidentity-api.jar
EXPOSE 9000
ENTRYPOINT ["java","-jar","/usr/local/lib/technoidentity-api.jar"]
