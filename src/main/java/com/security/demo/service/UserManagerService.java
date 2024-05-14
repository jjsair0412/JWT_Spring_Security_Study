package com.security.demo.service;

import com.security.demo.dto.JoinDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface UserManagerService {

    /**
     * 회원 가입
     * @param joinDto : join FormData
     */
    void joinProcess(JoinDto joinDto);

    /**
     * AccessToken, RefreshToken 재발급
     * @return : accessToken과 refreshToken 이 담긴 Map 반환
     */
    Map<String, String> accessTokenRefreshService(HttpServletRequest request, HttpServletResponse response);
}
