FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY blog-server-1.0-SNAPSHOT.jar app.jar

RUN mkdir -p /app/uploads

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
