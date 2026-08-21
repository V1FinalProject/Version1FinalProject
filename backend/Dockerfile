# Build context must be the repo root (not backend/), since this image
# needs both backend/ and the sibling data/ directory referenced by
# tagging.nominations-file in application.properties.
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY backend/.mvn/ .mvn/
COPY backend/mvnw backend/pom.xml ./
RUN ./mvnw dependency:go-offline
COPY backend/src/ src/
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/spring-hello-world-1.0.0.jar app.jar
COPY data/ /data
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
