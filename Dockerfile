FROM maven:3.8.1-openjdk-16 AS build
WORKDIR /src/app
COPY . .
RUN mvn clean package --projects jobotwar-web,jobotwar-model

FROM openjdk:16-slim AS final
WORKDIR /app
ENV DBURL="jdbc:h2:mem:jobotwar;INIT=RUNSCRIPT FROM 'classpath:sql/init.sql'"

# Creates a non-root user with an explicit UID and adds permission to access the /app folder
# For more info, please refer to https://aka.ms/vscode-docker-dotnet-configure-containers
RUN adduser -u 5678 appuser && chown -R appuser /app
USER appuser

COPY --from=build /src/app/jobotwar-web/target/*.jar .
COPY --from=build /src/app/jobotwar-web/target/lib ./lib
ENTRYPOINT java -Djdbc.drivers=org.postgresql.Driver -Ddb.url="${DBURL}" -jar /app/jobotwar-web-2.0-SNAPSHOT.jar
