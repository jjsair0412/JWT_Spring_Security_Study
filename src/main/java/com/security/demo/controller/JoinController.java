package com.security.demo.controller;

import com.security.demo.domain.dto.JoinDto;
import com.security.demo.service.UserManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinController {

    private final UserManagerService userManagerService;

    @PostMapping("/join")
    public String joinProcess(JoinDto joinDto) {
        userManagerService.joinProcess(joinDto);
        return "ok";
    }
}
