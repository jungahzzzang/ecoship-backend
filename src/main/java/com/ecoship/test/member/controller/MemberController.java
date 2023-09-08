package com.ecoship.test.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecoship.test.common.config.jwt.JwtProperties;
import com.ecoship.test.member.entity.Member;
import com.ecoship.test.member.entity.kakao.OAuthToken;
import com.ecoship.test.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class MemberController {
	
	//@Autowired
	//private MemberService memberService;
	
	/*
	 * 프론트에서 인가코드 돌려받는 주소
	 * 인가 코드로 access token 발급 -> 사용자 정보 조회 -> DB 저장 -> jwt 토큰 발급 -> 프론트에 토큰 전달
	 */
	
	// jwt 토큰으로 유저 정보 조회
	/*
	 * @GetMapping("/me") public ResponseEntity<Object>
	 * getCurrentMember(HttpServletRequest request) {
	 * 
	 * Member member = memberService.getMember(request);
	 * 
	 * return ResponseEntity.ok().body(member); }
	 */

}
