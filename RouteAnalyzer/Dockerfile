FROM gradle:8.10-jdk23 AS build

# Imposta la directory di lavoro
WORKDIR /app

COPY . .

RUN gradle build --no-daemon

# Comando per eseguire l'applicazione
ENTRYPOINT ["java", "-jar", "build/libs/RouteAnalyzer-1.0-SNAPSHOT-all.jar"]
