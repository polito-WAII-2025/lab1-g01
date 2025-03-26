FROM gradle:jdk17-corretto AS build

# Set the working directory
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradle/ ./

COPY src/ ./src

RUN gradle build --no-daemon

#Runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/build/libs/RouteAnalyzer-1.0-SNAPSHOT-all.jar /app/app.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
