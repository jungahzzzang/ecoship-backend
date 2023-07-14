package com.ecoship.test.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecoship.test.authentication.domain.AuthTokens;
import com.ecoship.test.authentication.service.OAuthLoginService;
import com.ecoship.test.config.auth.kakao.KakaoLoginParams;
import com.ecoship.test.config.auth.naver.NaverLoginParams;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final OAuthLoginService oAuthLoginService;
	
	@PostMapping("/kakao")
	public ResponseEntity<AuthTokens> loginKakao(@RequestBody KakaoLoginParams parmas) {
		return ResponseEntity.ok(oAuthLoginService.login(parmas));
	}
	
	@PostMapping("/naver")
    public ResponseEntity<AuthTokens> loginNaver(@RequestBody NaverLoginParams params) {
        return ResponseEntity.ok(oAuthLoginService.login(params));
    }
}
