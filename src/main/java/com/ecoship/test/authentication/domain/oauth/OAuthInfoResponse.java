package com.ecoship.test.authentication.domain.oauth;

/*
 * Access Token으로 요청한 외부 API 프로필의 응답값을
 * 우리 서비스 Model로 변환시키기 위한 인터페이스.
 */
public interface OAuthInfoResponse {

	String getEmail();
	String getNickname();
	OAuthProvider getOAuthProvider();
}
