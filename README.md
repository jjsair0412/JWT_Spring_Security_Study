# Spring Security Study - JWT 기반
## OverView

## 이론

## 실습
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
### 2. SecurityConfig.class 생성
Spring Security의 틀이되는 SecurityConfig.class 생성

JWT 기반 Spring Security를 생성할 때엔 , ***JWT 방식에서는 인증/인가 작업을 위해 세션을 Stateless 상태로 설정하는것이 중요함.***
```java
/**
 * 비밀번호를 캐시로 암호화시켜서 검증하고 진행하기 때문에 , BCryptPasswordEncoder 를 빈으로 등록하여
 * 해당 객체로 암호화
 */
@Bean
public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
}

/**
 * Spring Security 는 모든 컨트롤을 filterChain 에서 하게 됨.
 * 해당 설정이 Spring Security 의 가장 주요한 부분
 */
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    // http 객체를 통해 Spring Security 의 전반적 설정을 진행함.
    // csrf disable
    http.csrf((auth) -> auth.disable());
    // Form 로그인 방식 disable
    http.formLogin((auth) -> auth.disable());
    // http basic 인증 방식 disable
    http.httpBasic((auth) -> auth.disable());
    // 경로별 인가 작업 ( /admin , /main )
    http.authorizeHttpRequests(
            (auth) -> auth
                    .requestMatchers("/login", "/", "/join").permitAll()
                    .requestMatchers("/admin").hasRole("ADMIN")
                    .anyRequest().authenticated()
    );
    // 세션 설정 , jwt 방식에서는 인증/인가 작업을 위해 세션을 Stateless 상태로 설정하는것이 중요함.
    http.sessionManagement(
            (session) -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );
    return http.build();
}
```


### 3. DB 및 User Entity 생성
JWT 토큰을 발급하기 전 , 회원정보를 검증해야 함.

따라서 DB에 저장된 회원 정보와 , Form 에서 받은 username , password 를 비교해야 하기에 Spring Security에서 DB연결은 필수적임.
#### 3.1 MySql 배포
간단히 도커 컨테이너 생성
```
docker run --name security-mysql -e MYSQL_ROOT_PASSWORD=jinseongTest! -d -p 3306:3306 mysql:8.3
```

#### 3.2 Entity 생성
```ddl-create=auto``` 설정하여 SpringBoot Entity 기반으로 Table을 생성

``# Spring Security Study - JWT 기반
## OverView

## 이론

## 실습
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
### 2. SecurityConfig.class 생성
Spring Security의 틀이되는 SecurityConfig.class 생성

JWT 기반 Spring Security를 생성할 때엔 , ***JWT 방식에서는 인증/인가 작업을 위해 세션을 Stateless 상태로 설정하는것이 중요함.***
```java
/**
 * 비밀번호를 캐시로 암호화시켜서 검증하고 진행하기 때문에 , BCryptPasswordEncoder 를 빈으로 등록하여
 * 해당 객체로 암호화
 */
@Bean
public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
}

/**
 * Spring Security 는 모든 컨트롤을 filterChain 에서 하게 됨.
 * 해당 설정이 Spring Security 의 가장 주요한 부분
 */
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    // http 객체를 통해 Spring Security 의 전반적 설정을 진행함.
    // csrf disable
    http.csrf((auth) -> auth.disable());
    // Form 로그인 방식 disable
    http.formLogin((auth) -> auth.disable());
    // http basic 인증 방식 disable
    http.httpBasic((auth) -> auth.disable());
    // 경로별 인가 작업 ( /admin , /main )
    http.authorizeHttpRequests(
            (auth) -> auth
                    .requestMatchers("/login", "/", "/join").permitAll()
                    .requestMatchers("/admin").hasRole("ADMIN")
                    .anyRequest().authenticated()
    );
    // 세션 설정 , jwt 방식에서는 인증/인가 작업을 위해 세션을 Stateless 상태로 설정하는것이 중요함.
    http.sessionManagement(
            (session) -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );
    return http.build();
}
```


### 3. DB 및 User Entity 생성
JWT 토큰을 발급하기 전 , 회원정보를 검증해야 함.

따라서 DB에 저장된 회원 정보와 , Form 에서 받은 username , password 를 비교해야 하기에 Spring Security에서 DB연결은 필수적임.
#### 3.1 MySql 배포
간단히 도커 컨테이너 생성
```
docker run --name security-mysql -e MYSQL_ROOT_PASSWORD=jinseongTest! -d -p 3306:3306 mysql:8.3
```

#### 3.2 Entity 생성
```ddl-create=auto``` 설정하여 SpringBoot Entity 기반으로 Table을 생성

```Java
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String role;
}
```

application.properties 수정
```bash
spring.jpa.hibernate.ddl-auto=create
```
