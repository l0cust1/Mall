# 多阶段构建，先打包，再运行，节省镜像体积
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
# 打包，跳过测试，mall-web是你的启动模块
RUN mvn clean package -DskipTests

# 运行阶段，轻量jre
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 从构建阶段复制mall-web的jar包（注意：你的jar名字后面自己核对，一般是 mall-web-xxx.jar）
COPY --from=builder /app/mall-web/target/*.jar app.jar
# JVM限制内存，防止免费实例OOM
ENTRYPOINT ["java","-Xmx384m","-jar","app.jar"]
