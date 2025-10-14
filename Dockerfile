# Dockerfile
FROM eclipse-temurin:21

ARG JAR_FILE=target/BankProject-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} /BankProject-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/BankProject-0.0.1-SNAPSHOT.jar"]