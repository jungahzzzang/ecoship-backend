package com.ecoship.test.config.auth.kakao;

import com.ecoship.test.authentication.domain.oauth.OAuthInfoResponse;
import com.ecoship.test.authentication.domain.oauth.OAuthProvider;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)	// 필요없는 값들은 제외하고 원하는 값만 받도록함.
public class KakaoInfoResponse implements OAuthInfoResponse{
	
	@JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;
	
	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class KakaoAccount {
		private KakaoProfile profile;
		private String email;
	}
	
	@Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class KakaoProfile {
        private String nickname;
    }

	@Override
	public String getEmail() {
		return kakaoAccount.email;
	}

	@Override
	public String getNickname() {
		return kakaoAccount.profile.nickname;
	}

	@Override
	public OAuthProvider getOAuthProvider() {
		return OAuthProvider.KAKAO;
	}

}
