package com.security.demo.handler;

import com.security.demo.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
public class UserLogoutHandler implements LogoutHandler, LogoutSuccessHandler {

    private final JWTUtil jwtUtil;

    /**
     * 로그아웃 수행 메서드
     *
     * @param request : HttpServletRequest
     * @param response : HttpServletResponse
     * @param authentication : Authentication
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("logout 수행");
        String header = request.getHeader("Authorization");
        if( header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");

        }
        String accessToken = header.substring(7);

        jwtUtil.destroyToken(accessToken);
    }

    /**
     * 로그아웃 성공 시 호출 메서드
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();
        out.print("{\"success\": true}");
        out.flush();
    }
}
