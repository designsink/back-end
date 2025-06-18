FROM openjdk:17-jdk-slim
WORKDIR /app

# 이미 빌드된 JAR 복사
COPY build/libs/*.jar app.jar

# 업로드·설정 디렉터리
RUN mkdir -p /app/uploads /app/config \
    && chown -R 1000:1000 /app/uploads /app/config

EXPOSE 8080
VOLUME ["/app/uploads", "/app/config"]
ENV SPRING_CONFIG_LOCATION=classpath:/,file:/app/config/
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

USER 1000

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
