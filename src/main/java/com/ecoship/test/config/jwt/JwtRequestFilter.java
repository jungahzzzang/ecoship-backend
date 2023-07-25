package com.ecoship.test.config.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ecoship.test.member.repository.MemberRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
	
	@Autowired
	MemberRepository memberRepository;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		
		String jwtHeader = ((HttpServletRequest)request).getHeader(JwtProperties.HEADER_STRING);
		System.out.println("JwtRequestFilter 진입");
		
		//header가 정상적인 형식인지 확인
		if(jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		//jwt 토큰을 검증해서 정상적인 사용자인지 확인
		String token = jwtHeader.replace(JwtProperties.TOKEN_PREFIX, "");
		
		Long userCode = null;
		
		try {
			userCode = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
					.getClaim("id").asLong();
		} catch (TokenExpiredException e) {
            e.printStackTrace();
            request.setAttribute(JwtProperties.HEADER_STRING, "토큰이 만료되었습니다.");
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            request.setAttribute(JwtProperties.HEADER_STRING, "유효하지 않은 토큰입니다.");
        }
		
		request.setAttribute("userCode", userCode);
		
		filterChain.doFilter(request, response);
		
	}

}