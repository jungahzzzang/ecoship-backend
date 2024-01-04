package com.ecoship.test.oauth.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ecoship.test.jwt.JwtProperties;
import com.ecoship.test.jwt.JwtTokenUtils;
import com.ecoship.test.jwt.UserDetailsImpl;
import com.ecoship.test.member.dto.KakaoMemberInfo;
import com.ecoship.test.member.entity.Member;
import com.ecoship.test.member.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoMemberService {
	
	private final MemberRepository memberRepository;
	
	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String CLIENT_ID;
	
	@Value("${jwt.secretKey}")
	private static String SECRET_KEY;
	
	@Value("${jwt.issuer}")
	private String ISSUER;
	
	@Value("${jwt.userName}")
	private String CLAIM_USER_NAME;
	
	@Value("${jwt.tokenExpiry}")
	private String CLAIM_EXPIRED_DATE;
	
	@Transactional
	public void kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
		
		// 1.인가코드로 액세스 토큰 요청
		System.out.println("카카오 로그인 1번 접근");
		String accessToken = getAccessToken(code);
		
		// 2.토큰으로 카카오 API 호출
		System.out.println("카카오 로그인 2번 접근");
		KakaoMemberInfo kakaoMemberInfo = getKakaoMemberInfo(accessToken);
		
		// 3. 필요 시 회원가입
		System.out.println("카카오 로그인 3번 접근");
		Member member = registerKakaoMemberIfNeeded(kakaoMemberInfo);
		
		// 4. 강제 로그인 처리 & JWT 토큰 발행
		System.out.println("카카오 로그인 4번 접근");
		jwtTokenCreate(member, response);
	}
	
	public String getAccessToken(String code) throws JsonProcessingException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// HTTP Body 생성
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", CLIENT_ID);
		body.add("redirect_uri", "http://localhost:3000/user/kakao/callback");
        body.add("code", code);
        
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST, kakaoRequest, String.class);
        
        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper obj = new ObjectMapper();
        JsonNode json = obj.readTree(responseBody);
        return json.get("access_token").asText();
        
	}
	
	@Transactional
	public KakaoMemberInfo getKakaoMemberInfo(String accessToken) throws JsonProcessingException {
		
		// HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoMemberInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, kakaoMemberInfoRequest, String.class);
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String name = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();

        System.out.println("카카오 사용자 정보: " + id + ", " + name + ", " + email);
        return KakaoMemberInfo.builder()
                .kakaoId(id)
                .email(email)
                .nickname(name)
                .build();
	}
	
	public Member registerKakaoMemberIfNeeded(KakaoMemberInfo kakaoMemberInfo) {

		// DB에 중복된 kakao Id가 있는지 확인
		Long kakaoId = kakaoMemberInfo.getKakaoId();
		Member kakaoMember = memberRepository.findByKakaoId(kakaoId).orElse(null);
		boolean isSignUp = false;
		
		if(kakaoMember == null) {
			isSignUp = true;
			// 회원가입
			Random random = new Random();
			String kakaoDefaultName = "KAKAO" + random.nextInt(1000000000);
			String name = kakaoMemberInfo.getNickname();
			//String password = UUID.randomUUID().toString();
			String email = kakaoMemberInfo.getEmail();
			kakaoMember = Member.builder()
					.deleteCheck("N")
					.kakaoId(kakaoId)
					.nickname(kakaoDefaultName)
					.email(email)
					.name(name)
					.build();
			
			memberRepository.save(kakaoMember);
		}
		
		//String accessToken = jwtProvider.createToken(Long.toString(kakaoMember.getId()), kakaoMember.getEmail());
		//String refreshToken = jwtProvider.createRefreshToken(Long.toString(kakaoMember.getId()));
		
		return kakaoMember;
	}
	
	public void jwtTokenCreate(Member member, HttpServletResponse response) {
		
		UserDetailsImpl userDetails = new UserDetailsImpl(member);

        String token = JwtTokenUtils.createJwtToken(userDetails);
        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+ token);
	}

}
