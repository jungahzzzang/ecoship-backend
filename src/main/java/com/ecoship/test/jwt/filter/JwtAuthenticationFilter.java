package com.ecoship.test.jwt.filter;

import javax.management.RuntimeErrorException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	final private ObjectMapper objectMapper;
	
	public JwtAuthenticationFilter(final AuthenticationManager authenticationManager) {
		
		this.objectMapper = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		
		UsernamePasswordAuthenticationToken authenticationToken;
		
		try {
			
			JsonNode jsonNode = objectMapper.readTree(request.getInputStream());
			String username = jsonNode.get("username").asText();
			String password = jsonNode.get("password").asText();
			authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
			
		} catch (Exception e) {
			throw new RuntimeException("username , password 입력이 필요합니다.(JSON)");
		}
		
		setDetails(request, authenticationToken);
		return this.getAuthenticationManager().authenticate(authenticationToken);
	}

}
