package com.ecoship.test.common.config.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecoship.test.member.entity.Member;
import com.ecoship.test.member.repository.MemberRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	private final MemberRepository memberRepository;
	
	@Autowired
	public UserDetailsServiceImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		Member member = memberRepository.findByKakaoEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Can't find "+email));
		
		return new UserDetailsImpl(member);
	}

}
