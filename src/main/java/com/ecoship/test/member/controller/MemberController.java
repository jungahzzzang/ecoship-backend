package com.ecoship.test.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecoship.test.config.jwt.JwtProperties;
import com.ecoship.test.member.entity.Member;
import com.ecoship.test.member.entity.kakao.OAuthToken;
import com.ecoship.test.member.service.MemberService;

@RestController
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	@GetMapping("/login/oauth/kakao/callback")
	public ResponseEntity kakaoLogin(@RequestParam("code") String code) {
		
		//code : 카카오 서버로부터 받은 인가코드
		OAuthToken oAuthToken = memberService.getAccessToken(code);
		
		//발급받은 access token으로 카카오 회원 정보 DB 저장
		String jwtToken = memberService.saveMember(oAuthToken.getAccess_token());
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
		
		return ResponseEntity.ok().headers(headers).body("success");
	}

}
