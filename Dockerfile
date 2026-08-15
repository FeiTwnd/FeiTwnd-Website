# 构建阶段
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# 是否启用AI摘要模块（默认关闭；启用时以 -Pwith-ai 构建，将 langchain4j 与 AI 模块打进产物）
ARG AI_ENABLED=false

# 复制pom文件并下载依赖（AI_ENABLED=true 时提前下载 langchain4j 依赖，加快后续构建）
COPY Backend/pom.xml .
RUN if [ "$AI_ENABLED" = "true" ]; then \
      mvn dependency:go-offline -B -Pwith-ai; \
    else \
      mvn dependency:go-offline -B; \
    fi

# 复制源代码并构建
COPY Backend/ .
RUN if [ "$AI_ENABLED" = "true" ]; then \
      mvn clean package -DskipTests -B -Pwith-ai; \
    else \
      mvn clean package -DskipTests -B; \
    fi

# 运行阶段
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="feitwnd"

# 创建非root用户
RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -s /bin/sh -D appuser

WORKDIR /app

# 复制构建好的JAR包
COPY --from=builder /build/FeiTwnd-server/target/FeiTwnd-server-1.0-SNAPSHOT.jar ./feitwnd.jar

# 创建日志目录
RUN mkdir -p /app/logs && \
    chown -R appuser:appgroup /app

# 切换到非root用户
USER appuser

# JVM参数
# 4GB 内存的机器上还同时运行 MySQL/Redis/Nginx，堆上限收紧到 1GB，
# 改用 G1（比 ZGC 预留的额外内存更少），并限制堆外内存，避免整机内存被耗尽
ENV JAVA_OPTS="-XX:+UseG1GC -Xmx1024m -Xms256m -XX:MaxDirectMemorySize=256m -XX:MaxMetaspaceSize=256m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs"

# 暴露端口
EXPOSE 5922

# 启动命令
# spring.profiles.active 由 docker-compose 环境变量 SPRING_PROFILES_ACTIVE 传入
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar feitwnd.jar"]
