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
EXPOSE 9010
ENTRYPOINT ["java", \
            "-Dcom.sun.management.jmxremote=true", \
            "-Dcom.sun.management.jmxremote.port=9010", \
            "-Dcom.sun.management.jmxremote.local.only=false", \
            "-Dcom.sun.management.jmxremote.authenticate=false", \
            "-Dcom.sun.management.jmxremote.ssl=false", \
            "-Dcom.sun.management.jmxremote.rmi.port=9010", \
            "-Djava.rmi.server.hostname=localhost", \
            "-jar", "/usr/app/creek.jar"]