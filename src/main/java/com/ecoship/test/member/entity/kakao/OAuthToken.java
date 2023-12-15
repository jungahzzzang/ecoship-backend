package com.ecoship.test.member.entity.kakao;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthToken {

	 private String grantType;
	 private String accessToken;
	 private String refreshToken;
	 private Long accessTokenExpiresIn;
	 
	 public void tokenToHeaders(HttpServletResponse response) {
		 response.addHeader("Authorization", "Bearer "+getAccessToken());
		 response.addHeader("Refresh-Token", getRefreshToken());
		 response.addHeader("Access-Token-Expire-Time", getAccessTokenExpiresIn().toString());
	 }
}
