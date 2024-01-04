package com.ecoship.test.oauth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecoship.test.oauth.domain.Message;
import com.ecoship.test.oauth.domain.Success;
import com.ecoship.test.oauth.service.KakaoMemberService;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@CrossOrigin (origins = "http://localhost:3000" , exposedHeaders = "Authorization")
@RestController
@RequiredArgsConstructor
//@RequestMapping("/api")
public class OAuthController {
	
	private final KakaoMemberService kakaoMemberService;
	
	/*
	 * 프론트에서 인가코드 돌려받는 주소
	 * 인가 코드로 access token 발급 -> 사용자 정보 조회 -> DB 저장 -> jwt 토큰 발급 -> 프론트에 토큰 전달
	 */
	@GetMapping("/user/kakao/callback")
	public ResponseEntity kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException{
		
		try {
			kakaoMemberService.kakaoLogin(code, response);
			return new ResponseEntity<>("",HttpStatus.OK);
		}catch (Exception e) {
			e.getMessage();
		}
		return null;
	}
	
	/*
	 * JWT 토큰으로 유저 정보 요청
	 */
	/*
	 * @GetMapping("/api/user/me") public ResponseEntity<Object>
	 * getCurrentMember(HttpServletRequest request) {
	 * 
	 * Member member = kakaoMemberService.getMember(request);
	 * 
	 * return ResponseEntity.ok().body(member); }
	 */
}
