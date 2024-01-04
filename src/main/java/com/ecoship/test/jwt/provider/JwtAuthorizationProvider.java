package com.ecoship.test.jwt.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.ecoship.test.jwt.JwtDecoder;
import com.ecoship.test.jwt.UserDetailsImpl;
import com.ecoship.test.jwt.UserInfo;
import com.ecoship.test.member.entity.Member;
import com.ecoship.test.member.repository.MemberRepository;

@Component
public class JwtAuthorizationProvider implements AuthenticationProvider{
	
	private final JwtDecoder jwtDecoder;
	
	private final MemberRepository memberRepository;
	
	@Autowired
    public JwtAuthorizationProvider(JwtDecoder jwtDecoder, MemberRepository memberRepository){
        this.jwtDecoder=jwtDecoder;
        this.memberRepository=memberRepository;
    }

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String token = (String) authentication.getPrincipal();
        UserInfo userInfo = jwtDecoder.decodeUserName(token);

        Member member = memberRepository.findByNickname(userInfo.getUsername())
                .orElseThrow(()->new AuthenticationCredentialsNotFoundException("해당 회원정보가 없습니다."));
        UserDetailsImpl userDetails = new UserDetailsImpl(member);

        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
