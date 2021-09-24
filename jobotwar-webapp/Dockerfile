FROM mcr.microsoft.com/dotnet/aspnet:5.0-focal AS base
WORKDIR /app
EXPOSE 80

ENV ASPNETCORE_URLS=http://+:80

# Creates a non-root user with an explicit UID and adds permission to access the /app folder
# For more info, please refer to https://aka.ms/vscode-docker-dotnet-configure-containers
RUN adduser -u 5678 --disabled-password --gecos "" appuser && chown -R appuser /app
USER appuser

FROM mcr.microsoft.com/dotnet/sdk:5.0-focal AS build
WORKDIR /src
COPY ["Jobotwar.WebApp/Jobotwar.WebApp.csproj", "Jobotwar.WebApp/"]
RUN dotnet restore "Jobotwar.WebApp/Jobotwar.WebApp.csproj"
COPY . .
WORKDIR "/src/Jobotwar.WebApp"
RUN dotnet build "Jobotwar.WebApp.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "Jobotwar.WebApp.csproj" -c Release -o /app/publish

FROM nginx:alpine
COPY --from=publish /app/publish/wwwroot /usr/local/webapp/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
