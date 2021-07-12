FROM maven:3.8.1-openjdk-16 AS build
WORKDIR /src/app
COPY . .
RUN mvn clean package

FROM openjdk:16-jdk-oraclelinux8 AS final
WORKDIR /app
ENV DBURL="jdbc:h2:mem:jobotwar;INIT=RUNSCRIPT FROM 'classpath:sql/init.sql'"

# Creates a non-root user with an explicit UID and adds permission to access the /app folder
# For more info, please refer to https://aka.ms/vscode-docker-dotnet-configure-containers
RUN adduser -u 5678 appuser && chown -R appuser /app
USER appuser

COPY --from=build /src/app/jobotwar-web/target .
ENTRYPOINT ["java", "-Ddb.url=${DBURL}", "-jar", "/app/jobotwar-web-2.0-SNAPSHOT.jar"]
