package com.ecoship.test.oauth.util;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ecoship.test.member.entity.Member;
import com.ecoship.test.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class Validation {

	private final MemberRepository memberRepository;
	
	@Transactional(readOnly = true)
	public Member getPresentEmail(String email) {
		Optional<Member> member = memberRepository.findByEmail(email);
		return member.orElse(null);
	}
}
