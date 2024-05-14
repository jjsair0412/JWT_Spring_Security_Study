# Spring Security Study - JWT 기반
## OverView
JWT 기반 SpringSecutiry Study 코드
- refresh Token 으로 토큰검증

## Running
### 1. Dependency 추가
JWT 기반 Spring Security를 생성하기 위해 아래 Dependency를 추가해야 함.
1. Start WEB
2. starter-security
3. starter-data-jpa
4. db connector
5. jwt 
    - jjwt-api
    - jjwt-impl
    - jjwt-jackson

jwt 관련 의존성 패키지들은 , 버전에 굉장히 민감하게 반응함. 예를 들어 0.12.3 버전과 0.11.5 버전은 사용 메서드부터 JWT 토큰 생성 로직까지 많은부분이 다름.
따라서 잘 확인하고 작업해야함.


```bash
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
testImplementation 'org.springframework.security:spring-security-test'
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'
testImplementation 'org.springframework.boot:spring-boot-starter-test'
runtimeOnly 'com.mysql:mysql-connector-j'
testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

// jwt dependency
implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
implementation 'io.jsonwebtoken:jjwt-impl:0.12.3'
implementation 'io.jsonwebtoken:jjwt-jackson:0.12.3'
```
