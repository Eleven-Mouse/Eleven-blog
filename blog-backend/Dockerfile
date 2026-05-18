FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /workspace

COPY pom.xml ./
COPY blog-common ./blog-common
COPY blog-pojo ./blog-pojo
COPY blog-server ./blog-server

RUN mvn -pl blog-server -am -DskipTests package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /workspace/blog-server/target/blog-server-1.0-SNAPSHOT.jar app.jar

RUN mkdir -p /app/uploads

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD wget -q --spider http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-Xms256m", \
    "-Xmx768m", \
    "-XX:+UseG1GC", \
    "-XX:+HeapDumpOnOutOfMemoryError", \
    "-XX:HeapDumpPath=/app/heapdump.hprof", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
