package com.security.demo.Filter;

import com.security.demo.domain.dto.CustomUserDetails;
import com.security.demo.domain.dto.MemberEnum;
import com.security.demo.domain.entity.UserEntity;
import com.security.demo.domain.entity.UserTokenEntity;
import com.security.demo.jwt.JWTUtil;
import com.security.demo.repository.UserRepository;
import com.security.demo.repository.UserTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserTokenRepository userTokenRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        logger.info("Attempting to authenticate");

        String inputUsername = obtainUsername(req);
        String inputPassword = obtainPassword(req);


        // input으로 들어온 username, password로 UsernamePasswordAuthenticationToken 생성
        // 스프링 시큐리티에서 username과 password를 DB에 저장된 (회원가입된) 정보와 같은것을 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(inputUsername, inputPassword, null);

        // 검증 과정을 진행하기 위해 , AuthenticationManager 로 전달
        // SpringSecutiry 내부에서 검증로직이 수행됨.
        return authenticationManager.authenticate(authToken);

    }


    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
        // 로그인 검증 성공하면 해당 메서드 수행, jwt 반환로직 필요
        System.out.println("successful authentication");

        // 주입받은 Authentication 에서 검증이 완료된 CustomUserDetails 객체 반환
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();

        // username 파싱
        String username = customUserDetails.getUsername();

        // Authentication 의 getAuthorities 메서드로 Authentication 뽑아내서 , iterator로 내부 반복하여 userRole 뽑아냄
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority next = iterator.next();
        String userRole = next.getAuthority();


        // access jwt Token 생성, 1시간
        Map<String, String> jwtTokenMap = jwtUtil.createJwt(username, MemberEnum.valueOf(userRole));

        // refresh Token DB 저장
        UserEntity byUsername = userRepository.findByUsername(username);
        userTokenRepository.save(
                UserTokenEntity.builder()
                        .build()
                        .ofUserTokenEntity(byUsername, jwtTokenMap.get("refreshToken"))
        );

        // Bearer 토큰은 토큰값을 넣어줄 때 꼭 한칸 띄어주어야 함.
//         res.addHeader("Authorization", "Bearer " + jwtTokenMap.get("accessToken"));

        res.addHeader("accessToken", jwtTokenMap.get("accessToken"));
        res.addHeader("refreshToken", jwtTokenMap.get("refreshToken"));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res, AuthenticationException failed) throws IOException, ServletException {
        // 로그인 검증 실패하면 해당 메서드 수행, 401 에러 반환로직 필요
        System.out.println("unsuccessful authentication");
        res.setStatus(401);
    }

}
