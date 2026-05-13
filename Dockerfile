FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /app/target/traductor-1.0-SNAPSHOT.jar app.jar

# El puerto lo asigna Render dinámicamente mediante la variable PORT
EXPOSE ${PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]

