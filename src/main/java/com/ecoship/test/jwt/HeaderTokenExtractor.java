package com.ecoship.test.jwt;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class HeaderTokenExtractor {

	public String extract(String header) {
		/*
		 * 1. Token 값이 올바르지 않은 경우,
		 * 2. header 값이 비어있거나 HEADER_PREFIX 값보다 짧은 경우
		 * 예외 던져줌.
		 */
		if (header == null || header.equals("") || header.length() < JwtProperties.TOKEN_PREFIX.length()) {
			throw new NoSuchElementException("올바른 JWT 정보가 아닙니다.");
		}
		
		/*
		 * Token 값이 존재하는 경우
		 * [bearer ]부분만 제거 후 token 값 반환
		 */
		return header.substring(JwtProperties.TOKEN_PREFIX.length(), header.length());
	}
}
