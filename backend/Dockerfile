# ===== Stage 1: Build (Gradle + JDK 21) =====
FROM gradle:8.10.1-jdk21-alpine AS build
WORKDIR /app
# Copiamos todo el backend (build reproducible si tienes wrapper/lockfile)
COPY . .
# Construye el JAR de Spring Boot
RUN gradle bootJar --no-daemon

# ===== Stage 2: Runtime (JDK 21 slim) =====
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copiamos el JAR construido en la fase anterior
COPY --from=build /app/build/libs/*.jar app.jar

# Puerto de la app
EXPOSE 8080

# Activamos el perfil docker (se puede sobreescribir por env)
ENV SPRING_PROFILES_ACTIVE=docker

# Arranque
ENTRYPOINT ["java","-jar","/app/app.jar"]
