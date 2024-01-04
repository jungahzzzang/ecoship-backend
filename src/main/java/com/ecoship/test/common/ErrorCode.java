package com.ecoship.test.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

	// 400 Bad Request
	EMPTY_USERNAME(HttpStatus.BAD_REQUEST,"이메일을 입력해주세요."),
    EMPTY_PASSWORD(HttpStatus.BAD_REQUEST,"비밀번호를 입력해주세요."),
	
	// 401 UNAUTHORIZED 인증되지 않은 사용자
    AUTH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다"),
    INVALID_KAKAO_LOGIN_ATTEMPT(HttpStatus.UNAUTHORIZED, "카카오 로그인에 실패하였습니다");
	
	// 403 권한이 없는 사용자
	
	// 404 NOT FOUND
    
    private final HttpStatus httpStatus;
    private final String errorMessage;

    ErrorCode(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }
}
