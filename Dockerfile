FROM maven:3.9.16-eclipse-temurin-21-alpine AS builder
LABEL maintainer ="Basel_El_Rafei"
WORKDIR /app
COPY library/pom.xml .
RUN mvn dependency:go-offline
COPY library/src ./src
RUN mvn package -DskipTests
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app
ARG JAR_FILE=/app/target/*.jar
COPY --from=builder ${JAR_FILE} app.jar
EXPOSE 8080
CMD ["java","-jar","./app.jar"]
