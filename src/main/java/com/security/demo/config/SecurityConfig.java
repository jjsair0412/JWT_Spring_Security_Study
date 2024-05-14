package com.security.demo.config;

import com.security.demo.dto.MemberEnum;
import com.security.demo.jwt.JWTFilter;
import com.security.demo.jwt.JWTUtil;
import com.security.demo.jwt.LoginFilter;
import com.security.demo.repository.UserRepository;
import com.security.demo.repository.UserTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${spring.jwt.secret}")
    private String secret;

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final AuthenticationConfiguration authenticationConfiguration;

    /**
     * 비밀번호를 캐시로 암호화시켜서 검증하고 진행하기 때문에 , BCryptPasswordEncoder 를 빈으로 등록하여
     * 해당 객체로 암호화
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JWTUtil jwtUtil(){
        return new JWTUtil(secret);
    }

    @Bean
    public JWTFilter jwtFilter() throws Exception {
        return new JWTFilter(jwtUtil());
    }


    /**
     * Spring Security 는 모든 컨트롤을 filterChain 에서 하게 됨.
     * 해당 설정이 Spring Security 의 가장 주요한 부분
     *
     * Spring Secutiry 는 여러가지 필터가 연속적으로 작동하면서 로그인 로직이 수행됨.
     *
     * Filter 순서
     * 1. jwtFilter()
     *  - "Authorization" 헤더에 붙어온 Bearer 토큰으로 JWT 토큰이 유효한지, 유효하다면 username , role 을 토큰에서 꺼내옴
     *    유효하지 않을 경우 : permitAll() 해둔 특정 경로나 로그인이 필요하지 않은 경로 또는 JWT 토큰이 만료되었을 경우
     *
     * 2. LoginFilter()
     *  - request 에서 전달받은 username, password를 꺼내와서 실제 DB에 저장된 값과 검증, 검증할땐 "AuthenticationManager" 로 검증 수행.
     *    검증 성공한다면 "jwtUtil" 로 jwt 토큰 발급하여 return
     *    검증 성공안하면 401 에러 발생 및 특정 요구사항 수행
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // cors 설정.
        // security에서 설정하는 이유는 cors가 걸리면 JWT 토큰이 발급되지 않기 때문
        http.cors((cors) -> cors
                .configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();

                        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setMaxAge(3600L);

                        config.setExposedHeaders(Collections.singletonList("Authorization"));
                        return config;
                    }
                }));

        // csrf disable
        http.csrf((auth) -> auth.disable());
        // Form 로그인 방식 disable
        http.formLogin((auth) -> auth.disable());
        // http basic 인증 방식 disable
        http.httpBasic((auth) -> auth.disable());

        // 경로별 인가 작업 ( /admin , / )
        http.authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers("/login", "/", "/join").permitAll()
                        .requestMatchers("/admin").hasAuthority(MemberEnum.ROLE_ADMIN.name())
                        .anyRequest().authenticated()
        );

        // jwtFilter() 는 LoginFilter 앞에 먼저 jwt를 검증하도록 함.
        http.addFilterBefore(jwtFilter(), LoginFilter.class);

        // UsernamePasswordAuthenticationFilter 등록
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), userTokenRepository, userRepository, jwtUtil()), UsernamePasswordAuthenticationFilter.class);

        // 세션 설정 , jwt 방식에서는 인증/인가 작업을 위해 세션을 Stateless 상태로 설정하는것이 중요함.
        http.sessionManagement(
                (session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        return http.build();
    }
}
