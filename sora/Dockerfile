# 실행 이미지 설정
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 결과물 복사
COPY build/libs/sora-0.0.1-SNAPSHOT.jar sora.jar

# 애플리케이션 실행 명령어 설정
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "sora.jar"]

# 포트 노출
EXPOSE 8080
