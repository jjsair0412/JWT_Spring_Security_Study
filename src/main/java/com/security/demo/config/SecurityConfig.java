package com.security.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
}
