package com.security.demo.controller;

import com.security.demo.service.UserManagerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final UserManagerService userManagerService;

    /**
     * JWTFilter 에서 accessToken이 expired 되었을 경우, FrontEnd가 AccessToken 재발급 요청하는 API
     * 이때 FrontEnd는 헤더에 refreshToken 을 담아서 보낸다.
     * @return : accessToken
     */

    @GetMapping("/refresh")
    public Map<String,String> tokenRefresh(HttpServletRequest request, HttpServletResponse response) {
        return userManagerService.accessTokenRefreshService(request, response);
    }
}
