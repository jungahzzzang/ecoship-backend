package com.ecoship.test.authentication.oauth2;

import java.util.Map;

import com.ecoship.test.authentication.enums.AuthProvider;

public class OAuth2UserInfoFactory {

	public static OAuth2UserInfo getOAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes) {
		switch (authProvider) {
			case NAVER: return new NaverOAuth2User(attributes);
			case KAKAO: return new KakaoOAuth2User(attributes);
		default:
			throw new IllegalArgumentException("Invalid Provider Type.");
		}
	}
}
