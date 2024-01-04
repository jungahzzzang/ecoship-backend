package com.ecoship.test.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class JwtProcessingToken extends UsernamePasswordAuthenticationToken{

	public JwtProcessingToken(Object principal, Object credentials) {
		super(principal, credentials);
		// TODO Auto-generated constructor stub
	}

	public JwtProcessingToken(String token) {
		this(token, token.length());
	}
}
