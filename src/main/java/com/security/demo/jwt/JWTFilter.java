package com.security.demo.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.demo.commonResponse.customException.TokenExpiredException;
import com.security.demo.domain.dto.CustomUserDetails;
import com.security.demo.domain.dto.MemberEnum;
import com.security.demo.domain.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("INIT JWTFilter , JWT 검증 로직 수행");

        // AccessToken 꺼내 오기
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            // AccessToken 값이 요청에 없을경우 메서드 종료 수행
            // /join 등 토큰이 필요없는 경로, permitAll() 해둔 경로
            filterChain.doFilter(request,response);
            // 조건에 해당되면 메서드 종료
            return;
        }

        // Bearer 부분 짜르기 (7글자 잘라서 토큰만 꺼내오기)
        String AccessToken = authorization.substring(7);

        try  {
            if (!jwtUtil.isExpired(AccessToken)) {
                // Token 내부 username, role 꺼내오기
                String username = jwtUtil.getUsernameFromToken(AccessToken);
                MemberEnum role = jwtUtil.getRole(AccessToken);

                // UserDetail entity 객체에 접근한 유저 정보 담기
                // password 값은 토큰에 담기지 않았지만, 같이 초기화 시켜야하기 때문에 임시 비밀번호 강제로 넣어주면 됨.
                // DB에서 select 해서 넣어도 되지만 , 무작위 값이 들어가도 무관하기 때문에 DB 조회를 줄이기 위해 임시 비밀번호를 강제로 부여하거나 null을 입력
                CustomUserDetails customUserDetails = new CustomUserDetails(UserEntity.builder().build().ofUserEntity(username, "temppassword", role.toString()));

                // Spring Security 인증 토큰 생성
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

                // 세션에 사용자 등록
                SecurityContextHolder.getContext().setAuthentication(authToken);

                filterChain.doFilter(request,response);
            }

        } catch (TokenExpiredException e) {
            // AccessToken이 만료되었는지 확인하여 만료되었으면 refreshToken 만료되었는지 확인하여 FrontEnd는 AccessToken 재 발급 API 요청 필요
            log.info("AccessToken 만료");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            TokenExpiredException tokenException = new TokenExpiredException(HttpStatus.UNAUTHORIZED, "Access Token이 만료되었습니다.");
            new ObjectMapper().writeValue(response.getWriter(), tokenException);
        }


    }
}
