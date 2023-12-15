package com.ecoship.test.oauth.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ecoship.test.common.config.jwt.JwtProperties;
import com.ecoship.test.common.config.jwt.JwtProvider;
import com.ecoship.test.common.config.jwt.TokenDto;
import com.ecoship.test.common.config.jwt.UserDetailsImpl;
import com.ecoship.test.member.dto.KakaoMemberInfo;
import com.ecoship.test.member.entity.Member;
import com.ecoship.test.member.entity.kakao.KakaoProfile;
import com.ecoship.test.member.entity.kakao.OAuthToken;
import com.ecoship.test.member.repository.MemberRepository;
import com.ecoship.test.member.repository.RefreshTokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

public class KakaoMemberServiceBack {

	/*
	 * @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	 * private String clientId;
	 * 
	 * @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	 * private String redirectUri;
	 * 
	 * @Value("${spring.security.oauth2.client.registration.kakao.client-secret")
	 * private String clientSecret;
	 * 
	 * public void kakaoLogin(String code, HttpServletResponse response) throws
	 * JsonProcessingException {
	 * 
	 * String accessToken = getAccessToken(code);
	 * 
	 * KakaoMemberInfo kakaoMemberInfo = findProfile(accessToken);
	 * 
	 * Member member = saveMember(kakaoMemberInfo);
	 * 
	 * forceLogin(member);
	 * 
	 * String refreshToken = addKakaoMemberAuthorization(member, response);
	 * 
	 * refreshTokenRepository.save(new RefreshToken(member, refreshToken));
	 * 
	 * }
	 * 
	 * public String getAccessToken(String code) throws JsonProcessingException{
	 * 
	 * RestTemplate rs = new RestTemplate();
	 * 
	 * HttpHeaders headers = new HttpHeaders(); headers.add("Content-type",
	 * "application/x-www-form-urlencoded;charset=utf-8");
	 * 
	 * MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
	 * body.add("grant_type", "authorization_code"); body.add("client_id",
	 * clientId); body.add("redirect_uri", redirectUri); body.add("code", code);
	 * body.add("client_secret", clientSecret);
	 * 
	 * // 요청 보내기 HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new
	 * HttpEntity<>(body, headers);
	 * 
	 * ResponseEntity<String> accessTokenResponse = rs.exchange(
	 * "https://kauth.kakao.com/oauth/token", HttpMethod.POST, kakaoTokenRequest,
	 * String.class );
	 * 
	 * String responseBody = accessTokenResponse.getBody(); ObjectMapper obj = new
	 * ObjectMapper(); JsonNode jsonNode = obj.readTree(responseBody); String
	 * accessToken = jsonNode.get("access_token").asText();
	 * 
	 * return accessToken; }
	 * 
	 * public Member saveMember(KakaoMemberInfo kakaoMemberInfo) {
	 * 
	 * String kakaoEmail = kakaoMemberInfo.getEmail(); Member member =
	 * memberRepository.findByKakaoEmail(kakaoEmail).orElse(null);
	 * 
	 * if(member == null) {
	 * 
	 * member = Member.builder() .kakaoId(profile.getId())
	 * .kakaoProfileImg(profile.getKakao_account().getProfile().getProfile_image_url
	 * ()) .kakaoNickname(profile.getKakao_account().getProfile().getNickname())
	 * .kakaoEmail(profile.getKakao_account().getEmail())
	 * .userRole("ROLE_USER").build();
	 * 
	 * String nickname = kakaoMemberInfo.getNickname(); member = new
	 * Member(kakaoEmail, nickname); memberRepository.save(member); }
	 * 
	 * return member; }
	 * 
	 * public KakaoMemberInfo findProfile(String token) throws
	 * JsonProcessingException {
	 * 
	 * RestTemplate rs = new RestTemplate();
	 * 
	 * HttpHeaders headers = new HttpHeaders(); headers.add("Authorization",
	 * "Bearer " + token); headers.add("Content-type",
	 * "application/x-www-form-urlencoded;charset=utf-8");
	 * 
	 * HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new
	 * HttpEntity<>(headers);
	 * 
	 * // HTTP 요청 (POST 방식) 후, response 변수에 응답을 받음 ResponseEntity<String>
	 * kakaoProfileResponse = rs.exchange( "https://kapi.kakao.com/v2/user/me",
	 * HttpMethod.POST, kakaoProfileRequest, String.class );
	 * 
	 * ObjectMapper obj = new ObjectMapper(); String responseBody =
	 * kakaoProfileResponse.getBody(); ObjectMapper objectMapper = new
	 * ObjectMapper(); JsonNode jsonNode = objectMapper.readTree(responseBody);
	 * String nickname = jsonNode.get("properties").get("nickname").asText(); String
	 * email = jsonNode.get("kakao_account").get("email").asText();
	 * 
	 * return new KakaoMemberInfo(nickname, email); }
	 * 
	 * public Member getMember(HttpServletRequest request) {
	 * 
	 * Long userCode = (Long) request.getAttribute("userCode");
	 * 
	 * Member member = memberRepository.findByUserCode(userCode);
	 * 
	 * return member; }
	 * 
	 * private void forceLogin(Member member) {
	 * 
	 * //강제 로그인 처리 UserDetails userDetails = new UserDetailsImpl(member);
	 * Authentication authentication = new
	 * UsernamePasswordAuthenticationToken(userDetails, null,
	 * userDetails.getAuthorities());
	 * SecurityContextHolder.getContext().setAuthentication(authentication); }
	 * 
	 * private String addKakaoMemberAuthorization(Member member, HttpServletResponse
	 * response) {
	 * 
	 * TokenDto token = jwtProvider.createTokenDto(member);
	 * response.addHeader("Authorization", "BEARER "+token.getAccessToken());
	 * response.addHeader("refresh-token", token.getRefreshToken());
	 * 
	 * return token.getRefreshToken(); }
	 */

}
