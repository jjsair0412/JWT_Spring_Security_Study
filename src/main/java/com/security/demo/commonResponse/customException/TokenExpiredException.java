package com.security.demo.commonResponse.customException;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class TokenExpiredException extends RuntimeException {
    private HttpStatus httpStatus;
    private String errorMessage;
}
