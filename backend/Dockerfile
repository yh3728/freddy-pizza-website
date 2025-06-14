FROM gradle:jdk21 AS build
WORKDIR /workspace
COPY . .
RUN gradle clean build -x test --no-daemon
FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
COPY --from=build /workspace/src/main/resources /app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
