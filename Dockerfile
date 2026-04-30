# Runtime con Java 21
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia el jar construido por Maven (ajusta el nombre o usa wildcard)
COPY target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
