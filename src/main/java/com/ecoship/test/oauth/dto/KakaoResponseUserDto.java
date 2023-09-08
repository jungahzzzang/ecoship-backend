package com.ecoship.test.oauth.dto;

import com.ecoship.test.member.entity.Member;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class KakaoResponseUserDto {
	
	private String first;
	
	private Member member;

}
