FROM maven:3.9-eclipse-temurin-25 AS build

ARG timezone
ENV TZ=${timezone:-"America/Sao_Paulo"}

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/target/app.jar app.jar

EXPOSE 8080


#"-verbose:gc"
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+UseG1GC", "-XX:+AlwaysPreTouch", "-XX:ActiveProcessorCount=2", "-Djava.lang.Integer.IntegerCache.high=10000", "-XX:+UseNUMA", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]