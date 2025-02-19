# 바로고 Back-end 실무과제

## 프로젝트 환경
Language: Java(17)  
framework: Spring Boot(3.4.2)  
build tool: Gradle  
DB: H2

## DB 접속정보
path: localhost:8080/h2-console  
url: jdbc:h2:mem:barogo  
username: sa  
password: (없음)

## API 명세서
Spring Rest Docs로 구현했습니다.  
프로젝트 최상단 api-document 라는 디렉터리에 API명세서.html 파일로 미리 작성해놨습니다.  
아래 실행방법으로도 가능합니다.

### 실행방법
1. /gradlew clean build
2. **localhost:8080/docs/index.html** 접속 or **src/main/resources/static/docs/index.html** 파일 확인

---
### 추가로 말씀드리고 싶은 부분
1. 과제 특성상 직접 실행 해보실 수 있도록 JWT Secret Key를 환경 변수가 아닌 application.properties에 하드코딩했습니다.  
실제 운영 환경에서는 Secret Key를 환경 변수 또는 Secret Manager를 통해 안전하게 관리해야 합니다.  
###
2. 사전 과제의 특성상, 실행 환경을 간편하게 유지하기 위해 H2 인메모리 데이터베이스를 사용했습니다.


시간 내주셔서 감사합니다!
