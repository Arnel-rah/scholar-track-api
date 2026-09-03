FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /src
COPY gradlew ./
COPY gradle/ ./gradle/
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon || true
COPY src/ ./src/
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
USER app
WORKDIR /app
COPY --from=build /src/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]