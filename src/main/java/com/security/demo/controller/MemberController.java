package com.security.demo.controller;

import com.security.demo.service.UserManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final UserManagerService userManagerService;

    @GetMapping("/refresh")
    public String tokenRefresh() {
        return userManagerService.accessTokenRefreshService();
    }
}
