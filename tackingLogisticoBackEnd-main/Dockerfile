FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /src
COPY . .
WORKDIR /src/trackingLogisticoEap11
RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /src/trackingLogisticoEap11/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
