package com.security.demo.config;

import com.security.demo.domain.dto.MemberEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class KeycloakSecurityConfig {
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);

        // 경로별 인가 작업 ( /admin , / )
        http.authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers("/login", "/", "/join", "/logout", "/allowPage").permitAll()
                        .requestMatchers("/admin").hasAuthority(MemberEnum.ROLE_ADMIN.name())
                        .requestMatchers("/user").hasAuthority(MemberEnum.ROLE_USER.name())
                        .anyRequest().authenticated()
        );

        return http.build();
    }
}
