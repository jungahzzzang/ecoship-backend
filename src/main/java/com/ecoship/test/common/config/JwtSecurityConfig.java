package com.ecoship.test.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecoship.test.common.config.jwt.JwtProvider;
import com.ecoship.test.common.config.jwt.JwtRequestFilter;
import com.ecoship.test.common.config.jwt.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>{
	
	@Value("${jwt.secretKey}")
	private final String SECRET_KEY;
	private final JwtProvider jwtProvider;
	private final UserDetailsServiceImpl userDetailsService;
	
	@Override
	public void configure(HttpSecurity httpSecurity) throws Exception {
		
		JwtRequestFilter jwtRequestFilter = new JwtRequestFilter(SECRET_KEY, jwtProvider, userDetailsService);
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}

}
