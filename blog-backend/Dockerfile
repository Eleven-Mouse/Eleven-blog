
# 使用 Maven 镜像进行编译，别名为 builder
FROM maven:3.9.6-amazoncorretto-21 AS builder

# 设置工作目录
WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

# 2. 拷贝源代码并打包
COPY src ./src
# 打包，跳过测试 (-DskipTests)
RUN mvn clean package -DskipTests


# 使用轻量级的 JDK 运行时镜像
FROM eclipse-temurin:21-jre-alpine

# 设置工作目录
WORKDIR /app

COPY blog-server/target/*.jar app.jar

# 暴露后端端口
EXPOSE 8081

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
