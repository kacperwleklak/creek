FROM maven:3.8.3-openjdk-17 AS build

COPY cab/src /usr/src/app/cab/src
COPY cab/pom.xml /usr/src/app/cab/pom.xml

COPY common/src /usr/src/app/common/src
COPY common/pom.xml /usr/src/app/common/pom.xml

COPY creek-impl/src /usr/src/app/creek-impl/src
COPY creek-impl/pom.xml /usr/src/app/creek-impl/pom.xml

COPY reliable-channel/src /usr/src/app/reliable-channel/src
COPY reliable-channel/pom.xml /usr/src/app/reliable-channel/pom.xml

COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:17-oracle
COPY --from=build /usr/src/app/creek-impl/target/creek-impl*.jar /usr/app/creek.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/creek.jar"]