package com.security.demo.controller;

import com.security.demo.service.UserManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final UserManagerService userManagerService;

    /**
     * JWTFilter 에서 accessToken이 expired 되었을 경우, FrontEnd가 AccessToken 재발급 요청하는 API
     * @return : accessToken
     */
    @GetMapping("/refresh")
    public String tokenRefresh() {
        return userManagerService.accessTokenRefreshService();
    }
}
