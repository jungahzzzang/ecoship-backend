package com.ecoship.test.member.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ecoship.test.config.jwt.JwtProperties;
import com.ecoship.test.member.entity.Member;
import com.ecoship.test.member.entity.kakao.KakaoProfile;
import com.ecoship.test.member.entity.kakao.OAuthToken;
import com.ecoship.test.member.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MemberService {
	
	@Autowired
	MemberRepository memberRepository;
	
	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String clientId;
	
	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String redirectUri;
	
	@Value("${spring.security.oauth2.client.registration.kakao.client-secret")
	private String clientSecret;
	
	public OAuthToken getAccessToken(String code) {
		
		RestTemplate rs = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", clientId);
		body.add("redirect_uri", redirectUri);
		body.add("code", code);
		body.add("client_secret", clientSecret);
		
		// 요청 보내기
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
		
		ResponseEntity<String> accessTokenResponse = rs.exchange(
				"https://kauth.kakao.com/oauth/token",
				HttpMethod.POST,
				kakaoTokenRequest,
				String.class
		);
		
		//String responseBody = response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		OAuthToken oAuthToken = null;
		try {
			oAuthToken = objectMapper.readValue(accessTokenResponse.getBody(), OAuthToken.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return oAuthToken;
	}
	
	public String saveMember(String token) {
		
		KakaoProfile profile = findProfile(token);
		
		Member member = memberRepository.findByKakaoEmail(profile.getKakao_account().getEmail());
		
		if(member == null) {
			member = Member.builder()
						.kakaoId(profile.getId())
						.kakaoProfileImg(profile.getKakao_account().getProfile().getProfile_image_url())
						.kakaoNickname(profile.getKakao_account().getProfile().getNickname())
						.kakaoEmail(profile.getKakao_account().getEmail())
						.userRole("ROLE_USER").build();
			
			memberRepository.save(member);
		}
		
		return createToken(member);
	}
	
	public KakaoProfile findProfile(String token) {
		
		RestTemplate rs = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);
        
        // HTTP 요청 (POST 방식) 후, response 변수에 응답을 받음
        ResponseEntity<String> kakaoProfileResponse = rs.exchange(
    		"https://kapi.kakao.com/v2/user/me",
            HttpMethod.POST,
            kakaoProfileRequest,
            String.class
        );
        
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        
        try {
            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        return kakaoProfile;
	}
	
	public String createToken(Member member) {
		
		String jwtToken = JWT.create()
				.withSubject(member.getKakaoEmail())
				.withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
				.withClaim("id", member.getUserCode())
				.withClaim("nickname", member.getKakaoNickname())
				.sign(Algorithm.HMAC512(JwtProperties.SECRET));
		
		return jwtToken;
	}

}
