# Spring Security Study - JWT 기반
## OverView
Spring Security와 JWT를 사용하여 인증/인가 로직을 구현한 코드 

## 구현 내용
1. Spring Security + JWT 사용하여 username , password 를 input formData로 받은 후 , 로그인 성공 시 ```accessToken``` 및 ```refreshToken``` 발급 
   - ```refreshToken``` 은 BackEnd 에서 DB , Redis 등 다양한 저장소에 저장함.
     - 보관 이유 : 차후 ```accessToken``` 을 재발급 할 때 , 저장된 ```refreshToken ``` 을 FE가 BE로 보내고,  저장하고있는 ```refreshToken``` 값이 상이한지 비교, ***상이하다면 잘못된 접근***
   - 발급된 ```accessToken``` 과 ```refreshToken``` 은 FE에서 local 혹은 특정 스토리지에 저장하여 보관함.
   - 기본적으로 페이지 접근 시 ```accessToken``` 을 Authorization 헤더에 Bearer 형태로 넣어서 API에 요청하고, SpringSecurity는 Filter 계층에서 해당 토큰을 검증함.
     - 코드상 각 Token Expired 시간은 다음과 같음. ***(테스트가 용이하도록 짧게 설정함. 실제로는 refreshToken 2주 혹은 1개월 , accessToken 1시간 정도 부여함)***
     - ```accessToken``` : 1분
     - ```refreshToken``` : 5분
2. ```accessToken``` 만료 시 , Spring Security Filter에서 403 에러 혹은 특정 메시지 (ex. AccessToken 만료) return
   - 403 에러 혹은 특정 메시지 (ex. AccessToken 만료) 를 return 받은 FE는 , ```/refresh``` 등의 특정 API 호출
   - ```/refresh``` API는 DB에 저장된 refreshToken을 통해 사용자를 검증하고, 만료된 ```accessToken``` 을 재 발급하여 FE로 return.
     - 이때 ```refreshToken``` 의 만료시간도 검증함. ```refreshToken``` 또한 만료되었다면 ```refreshToken```, ```accessToken``` 모두 재 발급하여 FE로 return.
       - 재발급한 ```refreshToken``` 은 다시 DB , Redis 등 저장소에 저장.

## Running
### Requirement
버전 정보

| No | Name        | Version | ETC. |
|----|-------------|---------|------|
| 0  | MySQL       | 8.3     | -    |
| 1  | jwt package | 0.12.3  | -    |


backend DB는 MySQL 사용.
- 간단히 도커 컨테이너로 생성
```bash
# usecase
docker run --name {container_name} -e MYSQL_ROOT_PASSWORD={MYSQL_ROOT_PASSWORD} -d -p 3306:3306 mysql:8.3

# 실 사용 명령어
docker run --name security-mysql -e MYSQL_ROOT_PASSWORD=jinseongTest! -d -p 3306:3306 mysql:8.3
```


### 0. jdk version
jdk 17

### 1. Dependency 추가
JWT 기반 Spring Security 를 생성하기 위해 아래 Dependency를 추가해야 함.
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
