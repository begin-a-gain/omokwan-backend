# Liberica OpenJDK 17 기반의 경량 Alpine 이미지 사용
FROM bellsoft/liberica-openjdk-alpine:17

# 빌드된 JAR 파일의 이름을 ARG로 받음
ARG JAR_FILE=build/libs/omokwang-0.0.1-SNAPSHOT.jar

# JAR 파일을 도커 이미지에 복사
COPY ${JAR_FILE} app.jar

# 백그라운드로 실행할 필요가 없으므로 nohup을 사용하지 않고 ENTRYPOINT로 바로 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
