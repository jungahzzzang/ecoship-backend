package com.ecoship.test.authentication.domain;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.ecoship.test.config.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

/*
 * AuthTokens을 발급해주는 클래스
 */
@Component
@RequiredArgsConstructor
public class AuthTokensGenerator {

	private static final String BEARER_TYPE = "Bearer";
	private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;	//30분
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일
	
	private final JwtTokenProvider jwtTokenProvider;
	
	//memberId (사용자 식별값)을 받아 Access Token 생성
	public AuthTokens generate(Long memberId) {
        long now = (new Date()).getTime();
        Date accessTokenExpiredAt = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiredAt = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        String subject = memberId.toString();
        String accessToken = jwtTokenProvider.generate(subject, accessTokenExpiredAt);
        String refreshToken = jwtTokenProvider.generate(subject, refreshTokenExpiredAt);

        return AuthTokens.of(accessToken, refreshToken, BEARER_TYPE, ACCESS_TOKEN_EXPIRE_TIME / 1000L);
    }

	//Access Token에서 memberId(사용자 식별값) 추출
    public Long extractMemberId(String accessToken) {
        return Long.valueOf(jwtTokenProvider.extractSubject(accessToken));
    }
}