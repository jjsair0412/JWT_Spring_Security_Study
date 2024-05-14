package com.security.demo.commonResponse.customException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AccessToken 만료");

    private final HttpStatus httpStatus;
    private final String errorMessage;

}
