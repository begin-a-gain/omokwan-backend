# Alpine 기반의 Liberica OpenJDK 17 사용
FROM bellsoft/liberica-openjdk-alpine:17

# 빌드된 JAR 파일을 지정하는 ARG 설정
ARG JAR_FILE=build/libs/*.jar

# JAR 파일을 도커 이미지에 복사
COPY ${JAR_FILE} app.jar

# 도커 컨테이너를 시작할 때 실행할 명령어 설정
ENTRYPOINT ["java","-jar","/app.jar"]
