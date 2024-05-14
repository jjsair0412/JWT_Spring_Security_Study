package com.security.demo.service;

import com.security.demo.dto.JoinDto;

public interface UserManagerService {
    void joinProcess(JoinDto joinDto);
    String accessTokenRefreshService();
}
