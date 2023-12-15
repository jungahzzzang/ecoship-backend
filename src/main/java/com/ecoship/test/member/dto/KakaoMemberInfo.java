package com.ecoship.test.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoMemberInfo {

	private Long kakaoId;
	private String nickname;
	private String email;
}
