package com.ecoship.test.oauth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class KakaoResponseDto {

	private String jwtToken;
	private String currentTime;
	private String registerCheck;
	private String nickname;
}
