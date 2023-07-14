package com.ecoship.test.authentication.domain.oauth;

/*
 * OAuth 요청을 위한 Client 클래스
 */
public interface OAuthApiClient {
	
	OAuthProvider oAuthProvider();	//Client의 타입 반환
	String requestAccessToken(OAuthLoginParams params);	//Authorization Code를 기반으로 인증 API를 요청해서 Access Token 획득
	OAuthInfoResponse requestOAuthInfo(String accessToken);	//Access Token 기반으로 Email, Nickname이 포함된 프로필 정보 획득

}
