package com.ecoship.test.authentication.service;

import org.springframework.stereotype.Service;

import com.ecoship.test.authentication.domain.AuthTokens;
import com.ecoship.test.authentication.domain.AuthTokensGenerator;
import com.ecoship.test.authentication.domain.oauth.OAuthInfoResponse;
import com.ecoship.test.authentication.domain.oauth.OAuthLoginParams;
import com.ecoship.test.authentication.domain.oauth.RequestOauthInfoService;
import com.ecoship.test.member.entity.Member;
import com.ecoship.test.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

/*
 * 1. 카카오/네이버와 같은 OAuth 플랫폼에 인증 후 프로필 정보 가져오기
 * 2. email 정보로 사용자 확인(없으면 새로 가입 처리)
 * 3. Access Token 생성 후 내려주기
 */
@Service
@RequiredArgsConstructor
public class OAuthLoginService {

	private final MemberRepository memberRepository;
	private final AuthTokensGenerator authTokensGenerator;
	private final RequestOauthInfoService requestOAuthInfoService;
	
	public AuthTokens login(OAuthLoginParams params) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        Long memberId = findOrCreateMember(oAuthInfoResponse);
        return authTokensGenerator.generate(memberId);
    }

    private Long findOrCreateMember(OAuthInfoResponse oAuthInfoResponse) {
        return memberRepository.findByEmail(oAuthInfoResponse.getEmail())
                .map(Member::getId)
                .orElseGet(() -> newMember(oAuthInfoResponse));
    }

    private Long newMember(OAuthInfoResponse oAuthInfoResponse) {
        Member member = Member.builder()
                .email(oAuthInfoResponse.getEmail())
                .nickname(oAuthInfoResponse.getNickname())
                .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
                .build();

        return memberRepository.save(member).getId();
    }
}
