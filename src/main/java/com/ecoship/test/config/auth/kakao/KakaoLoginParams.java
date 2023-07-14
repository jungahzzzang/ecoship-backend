package com.ecoship.test.config.auth.kakao;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.ecoship.test.authentication.domain.oauth.OAuthLoginParams;
import com.ecoship.test.authentication.domain.oauth.OAuthProvider;

import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 카카오 API 요청에 필요한 인가코드를 갖고 있는 클래스.
 */

@Getter
@NoArgsConstructor
public class KakaoLoginParams implements OAuthLoginParams{
	
	private String authorizationCode;
	
	@Override
	public OAuthProvider oAuthProvider() {
		return OAuthProvider.KAKAO;
	}

	@Override
	public MultiValueMap<String, String> makeBody() {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("code", authorizationCode);
		return body;
	}

}
